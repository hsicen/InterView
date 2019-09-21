# 2019.9.5
View中的getContext一定返回的是Activity对象吗？

> 不一定是Activity对象
>
> 首先看一下View中`mContext`是在什么时候赋值的，在View类中`mContext`只在一个地方赋过值，那就是View初始化的时候；那么View的使用形式无非两种：
> **一种是**在代码中使用，那么这个Context就是我们传进去的Context，我们传进去的是什么就是什么
> **另一种是**在XML文件中使用，这时候就需要看系统帮我们做了什么。首先从 `setContentView()`方法进入，这时候系统调用了`AppCompatDelegate.setContentView(layoutResID)`，这是一个抽象方法，我们用`ctrl+h`查看它有哪些实现类，发现它的实现类只有一个`AppCompatDelegateImpl`,我们看看它的`setContentView(int resId)`
>
> ```Java
> public void setContentView(int resId) {
> 	ensureSubDecor();
> 	ViewGroup contentParent = (ViewGroupmSubDecor.findViewById(android.R.id.content);
> 	contentParent.removeAllViews();
> 	LayoutInflater.from(mContext).inflate(resId, contentParent);
> 	mOriginalWindowCallback.onContentChanged();
> }
> ```
>
> 首先会检查是否为`DecorateView`的子View，然后会找到`ContentView`并把ContentView的所有子View移除掉，接着让`LayoutInflater`解析XML文件，最后回调`Window#onContentChange()`方法。继续深入`LayoutInflater#inflate()`,通过源码发现最终会调用三个参数的inflate方法，在这个方法中我们发现起主要作用的是`rInflate()`，它是一个递归方法，去遍历XML文件中的结点，最终会调用`AppCompatViewInflater#createView()`方法，在这里面会把所有的View转换成AppCompat开头的View，我们随便点开一个以AppCompat开头的View会发现，当它们调用父类的构造方法时，context参数是使用`TintContextWrapper.wrap(context)`返回的Context，在这个方法里首先会调用`shouldWrap()`方法，如果这个方法返回True，就会返回一个`TintContextWrapper(context)`，这个时候我们调用`View#getContext()`方法返回的就不是Activity，而是`TintContextWrapper`对象了
>
> 那么是什么时候`shouldWrap()`会返回True呢？通过源码发现，当context没有被包装过并且SDK_VERSION小于21，就会返回True
>
> **总结：**
>
> - 通过`new View` `View.inflate()` `LayoutInflater.inflate()`生成的View，我们传参时传的什么Context，View中就是什么Context
> - 直接继承自Activity的Activity通过`setContentView()`构造出来的View，View中的Context就是Activity
> - 继承自AppCompatActivity的Activity通过`setContentView()`构造出来的View，并且是在5.0以下的手机上，View中的Context是`TintContextWrapper`，这个时候我们可以通过以下步骤拿到构造它的底层Activity对象
> ```Java
> if(context instanceof ContextWrapper){
> 	if(((ContextWrapper)context)).getBaseContext() instanceof Activity){
> 		//这样我们就拿到了底层的Activity对象
>   }
> }
> ```

