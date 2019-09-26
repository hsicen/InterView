# 2019.9.6  BuildConfig这个类是如何生成的？
### BuildConfig的用处
程序编译成功后，会在每一个Module下的`build/generated/source/buildConfig`目录下的对应环境包里生成一个BuildConfig文件，文件内容如下：
```Java
public final class BuildConfig {
  public static final boolean DEBUG = false;
  public static final String APPLICATION_ID = "com.hsc.core";
  public static final String BUILD_TYPE = "release";
  public static final String FLAVOR = "test";
  public static final int VERSION_CODE = 332;
  public static final String VERSION_NAME = "3.32";
  // Fields from product flavor: test
  public static final String HTTP_ANALYZE_URL = "https://k.hsc.com";
  public static final String HTTP_BASE_URL = "https://k.hsc.com";
}
```
从内容可以看出，BuildConfig是根据Module下的build.gradle生成的；其中最常用的是`BuildConfig.DEBUG`来判断是否处于debug模式，来控制日志的输出。这个值会根据开发者的Build类型自动设定，不需要手动设置

除此之外，还可以自定义添加BuildConfig里的常量，比如设置开发环境，测试环境和正式环境下的网络请求基地址和不同环境下的常量值

Module的`build.gradle`文件常见配置如下
```groovy
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'

android {
  //多环境配置
  flavorDimensions 'environment'

  compileSdkVersion versions.compileSdk
  buildToolsVersion versions.buildTools

  //通用性变量配置,不区分编译类型和编译环境
  defaultConfig {
		applicationId "com.hsicen.interview"
		minSdkVersion versions.minSdk
		targetSdkVersion versions.targetSdk
		versionCode versions.versionCode
		versionName versions.versionName
	}

	//签名配置
	signingConfigs {
		configRelease {
			def keystoreProps = new Properties()
			keystoreProps.load(project.rootProject                                        
					.file('keystore.properties').newDataInputStream())

			keyAlias keystoreProps.getProperty("keystore.alias")
			keyPassword keystoreProps.getProperty("keystore.password")
			storeFile file(keystoreProps.getProperty("keystore.path"))
			storePassword keystoreProps.getProperty("keystore.aliasPassword")
		}
  }

	//编译类型配置
	buildTypes {
		debug {
			minifyEnabled false
			zipAlignEnabled false
			shrinkResources false
			proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
		}

		release {
			minifyEnabled true
			zipAlignEnabled true
			shrinkResources true
			proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
			signingConfig signingConfigs.configRelease
		}
	}

	//多环境位置
	productFlavors {
		//开发环境
		dev {
			def flavorName = 'dev'

			//host配置
			buildConfigField "String", "BASE_URL", "\"${environment[flavorName].host}\""
			buildConfigField "String", "ANALYSE_URL", "\"${environment[flavorName].analyse}\""

			//key配置
			manifestPlaceholders = [
				APPLICATION_ID: appKey[flavorName].applicationId,
				AMAP_KEY      : appKey[flavorName].amap,
				UMENG_KEY     : appKey[flavorName].umeng,
				WEIXIN_KEY    : appKey[flavorName].weixin,
				IM_KEY        : appKey[flavorName].im,
				PUSH_KEY      : appKey[flavorName].push
			]
		}

		//测试环境
		tst {
			def flavorName = 'tst'

			//host配置
			buildConfigField "String", "BASE_URL", "\"${environment[flavorName].host}\""
			buildConfigField "String", "ANALYSE_URL", "\"${environment[flavorName].analyse}\""

			//key配置
			manifestPlaceholders = [
				APPLICATION_ID: appKey[flavorName].applicationId,
				AMAP_KEY      : appKey[flavorName].amap,
				UMENG_KEY     : appKey[flavorName].umeng,
				WEIXIN_KEY    : appKey[flavorName].weixin,
				IM_KEY        : appKey[flavorName].im,
				PUSH_KEY      : appKey[flavorName].push
			]
		}

		//预发布环境
		pre {
			def flavorName = 'pre'

			//host配置
			buildConfigField "String", "BASE_URL", "\"${environment[flavorName].host}\""
			buildConfigField "String", "ANALYSE_URL", "\"${environment[flavorName].analyse}\""

			//key配置
			manifestPlaceholders = [
				APPLICATION_ID: appKey[flavorName].applicationId,
				AMAP_KEY      : appKey[flavorName].amap,
				UMENG_KEY     : appKey[flavorName].umeng,
				WEIXIN_KEY    : appKey[flavorName].weixin,
				IM_KEY        : appKey[flavorName].im,
				PUSH_KEY      : appKey[flavorName].push
			]
		}

		//生成环境
		prd {
			def flavorName = 'prd'

			//host配置
			buildConfigField "String", "BASE_URL", "\"${environment[flavorName].host}\""
			buildConfigField "String", "ANALYSE_URL", "\"${environment[flavorName].analyse}\""

			//key配置
			manifestPlaceholders = [
				APPLICATION_ID: appKey[flavorName].applicationId,
				AMAP_KEY      : appKey[flavorName].amap,
				UMENG_KEY     : appKey[flavorName].umeng,
				WEIXIN_KEY    : appKey[flavorName].weixin,
				IM_KEY        : appKey[flavorName].im,
				PUSH_KEY      : appKey[flavorName].push
			]
		}
	}
}

//第三方库依赖
dependencies {
  implementation fileTree(dir: 'libs', include: ['*.jar'])
  implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$versions.kotlinVersion"
  implementation 'androidx.appcompat:appcompat:1.1.0'
  implementation 'androidx.core:core-ktx:1.1.0'
  implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
  testImplementation 'junit:junit:4.12'
  androidTestImplementation 'androidx.test:runner:1.2.0'
  androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
  
  implementation 'com.android.tools.build:gradle:3.5.0'
}
```

配置文件`config.gradle`如下
```groovy
ext {

    //多环境服务器地址配置
    environment = [
            dev: [
                    host   : "https://dev.hsc.com",
                    analyse: "https://dev.hsc.com"
            ],
            tst: [
                    host   : "https://test.hsc.com",
                    analyse: "https://test.hsc.com"
            ],
            pre: [
                    host   : "https://pre.hsc.com",
                    analyse: "https://pre.hsc.com"
            ],
            prd: [
                    host   : "https://product.hsc.com",
                    analyse: "https://product.hsc.com"
            ]
    ]

    //多环境Key配置
    appKey = [
            dev: [
                    applicationId: "com.hsc.app.dev",
                    umeng        : "38966ce8874f2212",
                    weixin       : "wx38966ce8874f2212",
                    amap         : "38966ce8874f2212",
                    im           : "38966ce8874f2212",
                    push         : "38966ce8874f2212"
            ],
            tst: [
                    applicationId: "com.hsc.app.test",
                    umeng        : "38966ce8874f2212",
                    weixin       : "wx38966ce8874f2212",
                    amap         : "38966ce8874f2212",
                    im           : "38966ce8874f2212",
                    push         : "38966ce8874f2212"
            ],
            pre: [
                    applicationId: "com.hsc.app.pre",
                    umeng        : "38966ce8874f2212",
                    weixin       : "wx38966ce8874f2212",
                    amap         : "38966ce8874f2212",
                    im           : "38966ce8874f2212",
                    push         : "38966ce8874f2212"
            ],
            prd: [
                    applicationId: "com.hsc.app",
                    umeng        : "38966ce8874f2212",
                    weixin       : "wx38966ce8874f2212",
                    amap         : "38966ce8874f2212",
                    im           : "38966ce8874f2212",
                    push         : "38966ce8874f2212"
            ]
    ]

    //版本号定义
    versions = [
            'compileSdk'   : 28,
            'buildTools'   : '28.0.3',

            'minSdk'       : 21,
            'targetSdk'    : 28,
            'versonCode'   : 100,
            'versionName'  : '1.0.0',

            'kotlinVersion': '1.3.50'
    ]
}

```
项目`build.gradle`文件配置如下
```groovy
buildscript { scriptHandler ->
    apply from: 'repositor.gradle', to: scriptHandler
    apply from: 'config.gradle'

    dependencies {
        classpath 'com.android.tools.build:gradle:3.5.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$versions.kotlinVersion"
    }
}

//项目仓库配置
allprojects {
    repositories {
        google()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```
`repositor.gradle`配置文件如下
```groovy
/*** Gradle仓库配置*/
repositories {
    maven { url "https://jitpack.io" }
    google()
    jcenter()
}
```

`buildConfigField "String", "BASE_URL", "\"${environment[flavorName].host}\""`

这句代码中三个参数分别表示数据类型，常量名，常量值；由于BuildConfig是通过String读取数据的，所以当常量值数据类型为String时，需要在双引号里再添加一个双引号；推荐在`gradle.properties`中定义常量值，然后直接在`build.gradle`中引用即可，因为`gradle.properties`里定义的value默认都是String，所以在定义build.gradle中可以直接使用


### BuildConfig的生成

为了能看到Gradle的源码，首先要添加以下依赖(版本跟当前项目的版本中的gradle版本一致)
```groovy
 implementation 'com.android.tools.build:gradle:3.5.0'
```

现在我们可以全局搜索一下关键字`BuildConfig.java`，会发现在`BuildConfigGenerator`中定义了该常量
```java
public static final String BUILD_CONFIG_NAME = "BuildConfig.java";
```

按住CTRL键，查看引用该常量的地方，可以发现在这个类的`generate()`方法中使用了它，来看一下精简后的代码
```java
public void generate() throws IOException {
        // 创建BuildConfig.java的File对象
        File buildConfigJava = new File(pkgFolder, BUILD_CONFIG_NAME);
        try {
            // 根据刚刚创建的File对象，创建JavaWriter对象，
            // 这个对象就是用来生成java源码文件的
            JavaWriter writer = closer.register(buildConfigJava);
            // 写入BuildConfig顶部的文档描述
            writer.emitJavadoc("Automatically generated file. DO NOT MODIFY")
                    // 写入包名
                    .emitPackage(mBuildConfigPackageName)
                    // 定义BuildConfig类
                    .beginType("BuildConfig", "class", PUBLIC_FINAL);

            // 遍历写入在build.gradle中定义的常量
            for (ClassField field : mFields) {
                emitClassField(writer, field);
            }
            // 完成
            writer.endType();
        } finally {
            closer.close();
        }
}
```

没错，`BuildConfig`这个类以及它里面的内容，就是在这个方法中利用square的开源项目 `javapoet - JavaWriter` 来生成的。

**那么BuildConfig.java是在哪个环节中生成的呢？**

> 我们可以顺藤摸瓜找到调用generate()的源头：
>
> GenerateBuildConfig.generate() ->
> TaskManager.createBuildConfigTask() ->
> (ApplicationTaskManager, LibraryTaskManager).createTasksForVariantScope -> 
> VariantManager.createAndroidTasks() ->
> BasePlugin.createAndroidTasks() -> 
> BasePlugin.createTasks() ->
> BasePlugin.basePluginApply() -> 
> BasePlugin.apply()
>
> 可以看到，调用的源头就是BasePlugin的apply方法，但是要搞清楚生成BuildConfig.java的任务执行时机，还是要看ApplicationTaskManager或LibraryTaskManager的createTasksForVariantScope 方法
>
> ```Java
> public void createTasksForVariantScope(@NonNull final VariantScope variantScope) {
>         createAnchorTasks(variantScope);
>         createCheckManifestTask(variantScope);
> 
>         // Add a task to publish the applicationId.
>         createApplicationIdWriterTask(variantScope);
> 
>         // Add a task to process the manifest(s)
>         createMergeApkManifestsTask(variantScope);
> 
>         // Add a task to create the res values
>         createGenerateResValuesTask(variantScope);
> 
>         // Add a task to merge the resource folders
>         createMergeResourcesTask(variantScope);
> 
>         // Add tasks to compile shader
>         createShaderTask(variantScope);
> 
>         // Add a task to merge the asset folders
>         createMergeAssetsTask(variantScope);
> 
>         // Add a task to create the BuildConfig class
>         createBuildConfigTask(variantScope);
> 
>         createAidlTask(variantScope);
> 
>         // Add a compile task
>         createCompileTask(variantScope);
> 
>         // Create the lint tasks, if enabled
>         createLintTasks(variantScope);
> }
> ```
> 可以看到是在createMergeResourcesTask和createMergeAssetsTask之后才执行的，完成之后，就开始生成`Aidl`文件的java代码了
