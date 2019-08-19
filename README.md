# Interview
每日一问记录


# 2019.8.16
事件分发机制大家应该都熟记于心，默认事件分发是逆序的，**有哪些方法可以修改分发顺序**？
> **修改事件分发顺序的话，在日常开发中基本遇不到，因为现在的逆序遍历，是跟View的层级显示相匹配的，随便更改反而不太合理。**
> 如果非要修改这个顺序，很多同学首先会想到：重写dispatchTouchEvent()方法，然后在里面一个for循环，从0开始一个个调用子View的dispatchTouchEvent()。
> **这个方法，不是说绝对不行，只是你要做的事情很多，就比如触摸坐标的转换**
> 我们都知道，View Group在分派事件的时候，会检查子View是否应用过属性动画的(位移、缩放、旋转等)，如果有的话还要把坐标给映射回去。
> 接着，还会把相对于这个View Group本身的触摸坐标 转换成 相对于对应子View的触摸坐标。这样说可能有点绕，
> 举个例子，比如：当手指在屏幕中按下，ViewGroup中收到的event坐标(getX,getY)假设是【500,500】，刚好在这个位置上有个子View，那接下来肯定会把事件传给这个子View的dispatchTouchEvent，这时候如果坐标不转换直接传的话，那子View收到的event坐标(getX,getY)也是【500,500】，这明显是不对的，正确的坐标应该要分别减去它的left和top。
> 这看起来好像没什么大的影响，但如果你的子View没有重写onTouchEvent方法的话（比如子View是常用的ImageView，TextView之类的），你的**OnClickListener**就会无效了，因为默认的onTouchEvent在处理**ACTION_MOVE**的时候，会检查event的坐标是否已经脱离了View的边界范围，如果在边界范围之外的话，pressed将会失效（认为没有被按下），当**ACTION_UP**时，如果pressed为false，就不会执行PerformClick。
> **那难道没有方法可以完美地做到了吗？**
> 在ViewGroup的dispatchTouchEvent方法中，虽然它是逆序的for，但是呢，它把子View拿出来的时候，却不是直接操作的mChildren数组，而是通过一个getAndVerifyPreorderedView方法来获得，这个方法会把当前索引传进去，还有一个preorderedList。如果传进去的preorderedList不为空，那么就会直接从它里面去取。
> **preorderedList怎么来？**  
> 通过调用buildOrderedChildList方法获取的。
> **buildOrderedChildList方法是怎么样的？**
> 它里面是通过一个getAndVerifyPreorderedIndex方法来获取对应的子View索引，这个方法要传进去一个叫customOrder的boolean。
> 这个**customOrder**，看名字可以知道，是自定义顺序的意思，如果它为true的话，接着会通过getChildDrawingOrder(int childCount, int i)方法来获取对应的索引，
> 而且，这个方法是protected的，所以我们可**以通过重写这个方法并根据参数"i"来决定返回哪一个View所对应的索引，从而改变分发的顺序。**
> 那这个customOrder，什么时候为true呢？
> 在buildOrderedChildList方法里可以看到这么一句：
> ```Java
> final boolean customOrder = isChildrenDrawingOrderEnabled();
> ```
> emmmm，也就是说，如果要自定义这个顺序的话，还需要调用setChildrenDrawingOrderEnabled(true)来开启。
> 重新捋一捋**流程**：
> 1. setChildrenDrawingOrderEnabled(true)来开启自定义顺序；
> 2. 重写getChildDrawingOrder方法来决定什么时候要返回哪个子View；

示例
>常用的SwipeRefreshLayout、ViewPager、RecyclerView都实现getChildDrawingOrder方法。其中RecyclerView还可以通过一个setChildDrawingOrderCallback方法来动态指定顺序，而不用重写RecyclerView。 


# 2019.8.17
匿名内部类访问的外部类局部变量**为什么要用final 修饰**，**jdk8为啥不需要了**？
1. 匿名内部类访问的外部类局部变量为什么要用final 修饰？
> 因为匿名内部类使用的是外部类局部变量的值，并非引用；通过反编译可以发现，外部类以及被访问的局部变量会通过构造方法传进去，对于局部变量，内部类使用的引用和外部类使用的并不是同一个，而如果局部变量不是final的话，当其中一方对其重新赋值就会导致内部类和外部类的数据不同步，所以要声明为final/
> **关于**外部类的全局变量为什么不用声明为final，是因为在内部类中是通过this来访问的，这个和外部类是同一个引用

2. jdk8为啥不需要了？
> jdk8其实使用了语法糖，自动加了final，其实和原来一样


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

