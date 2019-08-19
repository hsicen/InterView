# 2019.8.18
AppCompatTextView 与 TextView **怎么替换**，**为什么替换**
1. compat库是如何将TextView替换为AppCompatTextVew的？
>  TextView在运行时被替换成AppCompatTextView的前提是：该Activity必须继承自AppCompatActivity
>  **它是怎么替换的呢？**
>  我们给Activity设置布局一般会使用setContentView方法，打开AppCompatActivity，可以看到它已经重写了这个方法：
>
>  ```Java
>  @Override
>  public void setContentView(@LayoutRes int layoutResID) {
>  	getDelegate().setContentView(layoutResID);
>  }
>  ```
>  调用的是getDelegate方法返回的对象的setContentView方法。
>  getDelegate方法返回的是一个AppCompatDelegate对象，这个AppCompatDelegate是一个抽象类，由AppCompatDelegateImpl去实现，也就是说：getDelegate方法最终返回的是AppCompatDelegateImpl的实例。
>  那现在来看看它的setContentView方法是怎么实现的：
>  ```Java
>  @Override
>  public void setContentView(View v) {
>  ensureSubDecor();
>  ViewGroup contentParent = (ViewGroup) mSubDecor.findViewById(android.R.id.content);
>  contentParent.removeAllViews();
>  contentParent.addView(v);
>  mOriginalWindowCallback.onContentChanged();
>  }
>  ```
>  乍一看好像没什么特别的，就是把我们传进去的布局ID，给inflate出来并且把它添加到contentParent上而已。回到AppCompatActivity那边，看看它的onCreate方法：
>  ```Java
>  @Override
>  protected void onCreate(@Nullable Bundle savedInstanceState) {
>  ......
>  getDelegate().installViewFactory();
>  ......
>  }
>  ```
>  点进去：
>  ```Java
>  @Override
>  public void installViewFactory() {
>  	LayoutInflater layoutInflater = LayoutInflater.from(mContext);
>  	if (layoutInflater.getFactory() == null) {
>  		LayoutInflaterCompat.setFactory2(layoutInflater, this);
>  	}
>  	......
>  }
>  ```
>  可以看到AppCompatDelegateImpl在这个方法中会给LayoutInflater设置一个Factory2，并且传的是this，说明它是实现了Factory2的。
>  我们知道，当LayoutInflater在inflate布局的时候，会优先调用Factory2的onCreateView方法
>  那现在来看看AppCompatDelegateImpl的onCreateView方法：
>  ```Java
>  @Override
>  public View createView(View parent, String name, Context context, AttributeSet attrs) {
>  	......
>  	return mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
>              IS_PRE_LOLLIPOP,  true,  VectorEnabledTintResources.shouldBeUsed());
>  }
>  ```
>  可以看到它把createView的工作交给了AppCompatViewInflater，来看看它是怎么实现的：
>  ```Java
>  final View createView(View parent, final String name, Context context, AttributeSet attrs, boolean inheritContext,boolean readAndroidTheme, boolean readAppTheme, boolean wrapContext) {
>  	switch (name) {
>  		case "TextView":
>  			view = createTextView(context, attrs);
>  			verifyNotNull(view, name);
>  			break;
>  			......
>  	}
>  	return view;
>  }
>  
>  protected AppCompatTextView createTextView(Context context, AttributeSet attrs) {
>  return new AppCompatTextView(context, attrs);
>  }
>  ```
> emmm，在这个方法中，会把常用的View(ViewGroup除外)替换成对应的AppCompat开头的View，除了TextView，还有ImageView、EditText、SeekBar等等。

2. 为什么要进行替换？
> 为了向下兼容，高版本的特性在低版本上也可以使用；比如我们来观察一下各个AppCompat开头的组件，可以发现他们的共同点：都实现了一个叫TintableBackgroundView的接口。看看它里面有哪些方法：
> ```Java
> void setSupportBackgroundTintList(ColorStateList tint);
> ColorStateList getSupportBackgroundTintList();
> 
> void setSupportBackgroundTintMode(PorterDuff.Mode tintMode);
> PorterDuff.Mode getSupportBackgroundTintMode();
> ```
> 那现在可以大概猜到，替换成AppCompat系列的，就是为了能够让旧版本(5.0以下)能够兼容一个叫BackgroundTint的东西，中文翻译为 背景着色。item被选中后的变色效果，就是用BackgroundTint来做的！


3. 根据替换相关原理，我们可以做哪些事情？
> 最最广为人知的，就是主题替换了。
还可以通过 “替换”，来做出动态控制View属性的效果，这也跟主题替换差不多，但相比于一般的图片，颜色，背景替换，我们还可以做出更加惊喜的效果。
