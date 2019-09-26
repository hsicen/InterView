## R.Java文件的生成规则是什么

#### 同一资源文件在不同的module下引用，大概率下资源id不同，那么最终打包是如何处理的？
**结论：**最终打包出来的那些资源文件id，是会重新分配的(同一资源生成的id，就算不在同一R.class中，打包出来的id也是相同的)

**下面做一个测试：**

> 1. app Module 依赖 test Module
> 2. test模块中的string.xml有个test_value字符资源
> 3. 在test模块引用这个资源文件，查看资源id为-1900022
> 4. 在app模块引用这个资源文件，查看资源id为-1900023
>
> 这个时候可以看出,相同的资源文件确实有可能出现id不相同的情况,打包成apk文件后,把它拖进AndroidStudio中,打开classes.dex
> - 首先找到app包名下的R$stirng，右键查看字节码，发现test_value的id值由原来的-1900023变成了0x7f10005d
> - 接着找到test模块包名下的R$stirng，查看字节码发现id值也变成了0x7f10005d
>
> 由此看来，id值在打包成apk的时候，确实重新分配了，而且同一资源的id值也统一了

#### 源码分析：如何重新分配这些资源的id值的
上次分析BuildConfig文件的生成过程时了解到，很多任务都是在ApplicationTaskManager里的createTasksForVariantScope方法中创建的，现在再来看一下这个方法：
```Java
@Override
public void createTasksForVariantScope(
	@NonNull final VariantScope variantScope, @NonNull List<VariantScope> variantScopesForLint) {
        ......
        // Add a task to create the BuildConfig class
        createBuildConfigTask(variantScope);
        // Add a task to process the Android Resources and generate source files
        createApkProcessResTask(variantScope);
        ......
}
```
可以看到在创建了BuildConfigTask任务后，接着调用了createApkProcessResTask()方法，查看注释，可以知道这个任务是用来处理资源和生成源文件的，一步步点进去看看最终处理逻辑：
> createApkProcessResTask() ->
> createProcessResTask() ->
> createNonNamespacedResourceTasks() ->
> GenerateLibraryRFileTask.doFullTaskAction() ->
> GenerateLibRFileRunnable.run() ->
> SymbolExportUtils.processLibraryMainSymbolTable()

查看SymbolExportUtils.processLibraryMainSymbolTable()方法：
```kotlin
fun processLibraryMainSymbolTable() {
    ......
    val tablesToWrite = processLibraryMainSymbolTable()
    // Generate R.java files for main and dependencies
    tablesToWrite.forEach { SymbolIo.exportToJava(it, sourceOut, false) }
    ......
}
```

看中间的的注释，可以确定下一句代码就是用来生成R.java文件的，它会为tablesToWrite里面的每一个item都生成一个R文件，看下tablesToWrite是怎么来的：
```kotlin
internal fun processLibraryMainSymbolTable(): List<SymbolTable> {
    // Merge all the symbols together.
    // We have to rewrite the IDs because some published R.txt inside AARs are using the
    // wrong value for some types, and we need to ensure there is no collision in the
    // file we are creating.
    val allSymbols: SymbolTable = mergeAndRenumberSymbols(
        finalPackageName, librarySymbols, depSymbolTables, platformSymbols
    )

    val mainSymbolTable = if (namespacedRClass) allSymbols.filter(librarySymbols) else allSymbols

    // Generate R.txt file.
    Files.createDirectories(symbolFileOut.parent)
    SymbolIo.writeForAar(mainSymbolTable, symbolFileOut)

    val tablesToWrite =
        RGeneration.generateAllSymbolTablesToWrite(allSymbols, mainSymbolTable, depSymbolTables)
    return tablesToWrite
}
```

看开头的注释：**“We have to rewrite the IDs ”**，就知道是必须重写这些id的意思，再看一下接下来调用的mergeAndRenumberSymbols()方法：
```kotlin
fun mergeAndRenumberSymbols(): SymbolTable {
    ......
    // the ID value provider.
    val idProvider = IdProvider.sequential()
    ......
}
```

可以看到调用了IdProvider.sequential()方法，这个方法是用来提供id的，看下它里面是怎样实现的：
```kotlin
fun sequential(): IdProvider {
        return object : IdProvider {
            private val next = ShortArray(ResourceType.values().size)

            override fun next(resourceType: ResourceType): Int {
                val typeIndex = resourceType.ordinal
                return 0x7f shl 24 or (typeIndex + 1 shl 16) or (++next[typeIndex]).toInt()
            }
        }
}
```

可以发现，产生的id都会0x7f开头的，我们刚开始打包后资源文件的id值也是0x7f开头的，到这里基本可以确定，最终打包的资源id，就是通过这个IdProvider的匿名子类来重新创建的，而且**同一个资源所对应的id也是一样的**



### 项目中同名资源，会不会覆盖，规则是怎么样的？
我们来做一个测试：
> 1. app Module中**有**test_value 资源，依赖了同样有test_value资源的module1
> 2. app Module中**有**test_value 资源，依赖了同样有test_value资源的module1，module2
> 3. app Module中**无**test_value 资源，先后依赖了有test_value资源的module1，module2
> 4. app Module中**无**test_value 资源，先后依赖了有test_value资源的module2，module1
>
> 然后打包apk，拖进AndroidStudio，点开resources.arsc文件，定位到test_value，会看到以下对应结果：
> 1. test_value的值是app中的值
> 2. test_value的值是app中的值
> 3. test_value的值是module1中的值
> 4. test_value的值是module2中的值
>

**结论：**多模块开发中，不同模块间如果有同名资源，那么最终采纳的优先级为：app的优先级要高于依赖的module，而module之间的优先级则由`app/build.gradle`文件中dependencies的**implementation**顺序决定的

##### 那具体是怎么做到的呢？源码分析
打开ApplicationTaskManager，找到createTasksForVariantScope()方法，会发现：
```java
public void createTasksForVariantScope() {
        ......
        createGenerateResValuesTask(variantScope);
        createMergeResourcesTask(variantScope);
        ......
}
```

在createGenerateResValuesTask任务创建后，接着会创建createMergeResourcesTask任何，这个任务就是用来合并资源的，我们一级一级的点进去，找到最终处理逻辑的地方：
> TaskManager.basicCreateMergeResourcesTask() ->
> MergeResources.CreationAction() ->
> MergeResources.doFullTaskAction()

接着我们来看一下MergeResources的doFullTaskAction()方法：
```java
protected void doFullTaskAction() throws IOException, JAXBException {
        ......
        // create a new merger and populate it with the sets.
        ResourceMerger merger = new ResourceMerger(minSdk.get());
        ......
        Blocks.recordSpan(GradleBuildProfileSpan.ExecutionType.TASK_EXECUTION_PHASE_2,
                () -> merger.mergeData(writer, false /*doCleanUp*/));
        ......
}
```

可以看到在执行到第2阶段的时候，传进去的lambda会调用ResourceMerger的mergeData()方法,点进这个方法，我们看看合并数据的逻辑是怎样的：
```java
public void mergeData(MergeConsumer<I> consumer, boolean doCleanUp) {
	// get all the items keys.
	Set<String> dataItemKeys = new HashSet<>();
                    
	//遍历资源集，并把全部资源名添加到dataItemKeys中
	for (S dataSet : mDataSets) {
		// quick check on duplicates in the resource set.
            dataSet.checkItems();
            ListMultimap<String, I> map = dataSet.getDataMap();
            dataItemKeys.addAll(map.keySet());
	}

	//遍历刚刚添加的全部资源名
	for (String dataItemKey : dataItemKeys) {
		I toWrite = null;

		//倒序遍历，查找存在相同名字的item
		setLoop: for (int i = mDataSets.size() - 1; i >= 0; i--) {
			S dataSet = mDataSets.get(i);
			 // look for the resource key in the set
			ListMultimap<String, I> itemMap = dataSet.getDataMap();
			//不存在，开始下一轮查找
			if (!itemMap.containsKey(dataItemKey)) {
                    continue;
                }
                
                List<I> items = itemMap.get(dataItemKey);
                //list没内容，开始下一轮查找
                if (items.isEmpty()) {
                	continue;
                }

                //倒序遍历
                for (int ii = items.size() - 1; ii >= 0; ii--) {
                    I item = items.get(ii);

                    if (toWrite == null) {
                        toWrite = item;
                    }

                    if (toWrite != null) {
                        //这里跳出到爸爸层循环
                        //也就是上面“查找存在相同名字的item”的循环
                        break setLoop;
                    }
                }
            }

            // now need to handle, the type of each (single res file, multi res file), whether
            // they are the same object or not, whether the previously written object was
            // deleted.

            if (toWrite == null) {
                // nothing to write? delete only then.
            } else {
                //看下面的原注释: "替换成另一个资源。强行把新的值写进去"，证明同名的资源值是在这里替换的
                // replacement of a resource by another.
                // force write the new value
                toWrite.setTouched();
                consumer.addItem(toWrite);

                // and remove the old one  移除掉旧的
                consumer.removeItem(previouslyWritten, toWrite);
            }
        }
}
```

可以看到，它首先会遍历一个**装有全部资源名字**的List，并将符合条件资源的key添加到一个新的Set集合中，然后倒序遍历这个Set集合，并在里面倒序遍历一个**装有全部资源**的List，然后逐个检查有没有和外面遍历到的item同名的，如果有同名的，会用里面item的值替换外层那个同名item的值

**那么这个装有全部资源的List是怎么来的？里面装的都是什么？**

可以先做个猜测：既然在mergeData方法中会**倒序**查找同名的资源，而在我们上面的测试中，app的优先级要比modul高，那么，这个list会不会就是【module2, module1, module0, app】这样排序的呢？如果是的话，就刚好能对应刚刚的测试结果

回到**MergeResources**的doFullTaskAction方法中，会看到这一段代码（merger就是刚刚调用`mergeData`方法的ResourceMerger）：
```java
for (ResourceSet resourceSet : resourceSets) {
	resourceSet.loadFromFiles(new LoggerWrapper(getLogger()));
	merger.addDataSet(resourceSet);
}
```

可以看到，它遍历了resourceSets，把全部的元素添加到了ResourceMerger(上面的mDataSets)中，找到resourceSets，可以发现它是通过getResourceComputer的compute()方法获取的：
```java
fun compute(precompileRemoteResources: Boolean = false): List<ResourceSet> {
        // app中的资源集
        val sourceFolderSets = getResSet()

        val resourceSetList = ArrayList<ResourceSet>(size)

        // add at the beginning since the libraries are less important than the folder based
        // resource sets.
        // get the dependencies first
        // libraries里面装有各个依赖库的相关数据
        libraries?.let {
            val libArtifacts = it.artifacts

            for (artifact in libArtifacts) {
                val resourceSet = ResourceSet()
                resourceSet.isFromDependency = true
                resourceSet.addSource(artifact.file)
                // 每次添加元素在最前面
                // add to 0 always, since we need to reverse the order.
                resourceSetList.add(0, resourceSet)
            }
        }

        // 最后，添加app里面的资源
        // add the folder based next
        resourceSetList.addAll(sourceFolderSets)

        return resourceSetList
}
```

可以看到，在添加依赖库的资源时，采用了头插法，如果原来依赖的顺序是【module0，module1，module2】，那么当遍历完成后resourceSetList里面的元素就是【module2，module1，module0】，在最后还添加了app中的资源集，默认添加在了集合的末尾

这样一来，也就对应了我们刚才的猜想：app的资源集在resourceSetList的最后面，那么在合并资源，倒序遍历时也就会先找到app里面的资源，其次是modu0，module1.....
