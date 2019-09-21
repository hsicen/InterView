# 2019.9.3

##### 为什么属性动画移动一个控件后，目标位置仍然能响应用户事件？
>根据题意，应用了属性动画之后，该View依然可以正确的接收到事件的分发；那么我们就要搞清楚ViewGroup是怎么找到这个“偷跑”了的View
>
>我们知道，在调用了View的translationXXX等分发后，虽然在屏幕上的位置变了，但是它的ViewGroup中[left,top,right,bottom]是不会变的
>
>分析ViewGroup的事件分发流程：当我们的手指按下时，触摸事件会经过ViewGroup的dispatchTouchEvent()方法筛选符合条件的子View进行事件的分发；那么我们就来看一看ViewGroup是如何筛选符合条件的子View的
>```java
>if (onFilterTouchEventForSecurity(ev)) {
>	//符合安全性的触摸事件才给予处理
>}
>```
>首先会通过```onFilterTouchEventForSecurity(ev)```方法对触摸事件的安全性进行判断，符合安全政策的触摸事件才会给予处理，这里可能有人会问什么是符合安全政策的触摸事件呢？
>```java
>public boolean onFilterTouchEventForSecurity(MotionEvent event) {
>	//noinspection RedundantIfStatement
>	if ((mViewFlags & FILTER_TOUCHES_WHEN_OBSCURED) != 0 && (event.getFlags() & MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0) {
>		// Window is obscured, drop this touch.
>		return false;
>	}
>	return true;
>}
>```
>通过该方法的源码我们发现，其实屏幕关闭的时候系统也是可以监听到我们的触摸事件的，不过系统出于安全性考虑，系统把该事件给拦截了，并没有分发出来
>
>在经过安全性判断过后，然后会判断事件的类型
>```java
>if (actionMasked == MotionEvent.ACTION_DOWN) {
>	// Throw away all previous state when starting a new touch gesture.
>	// The framework may have dropped the up or cancel event for the previous gesture
>	// due to an app switch, ANR, or some other state change.
>	cancelAndClearTouchTargets(ev);
>	resetTouchState();
>}
>```
>如果是ACTION_DOWN事件，会清除数据和重置状态
>
>然后会判断该事件是否会被父View拦截进行拦截的相关处理，经过一系列的处理，接下来会先拿到一个```preorderedList```,根据名字我们知道这个preorderedList是一个优先级列表，所以要实现事件分发的优先级这个preorderedList可能会是一个切入点；这里已经拿到了childView列表了，接下来就要进行资格判断了，符合资格的childView才会获取到该事件
>```Java
>final View child = getAndVerifyPreorderedView(preorderedList, children, childIndex);
>
>//child焦点验证
>if (childWithAccessibilityFocus != null) {
>	if (childWithAccessibilityFocus != child) {
>		continue;
>	}
>
>	childWithAccessibilityFocus = null;
>	i = childrenCount - 1;
>}
>
>//child 点击范围验证
>if (!canViewReceivePointerEvents(child) || !isTransformedTouchPointInView(x, y, child, null)) {
>	ev.setTargetAccessibilityFocus(false);
>	continue;
>}
>
>//以上验证通过，新的target找到
>newTouchTarget = getTouchTarget(child);
>```
>首先会对child进行焦点性验证，然后再对触摸范围进行验证，在```canViewReceivePointerEvents()```方法中，会判断该child能否接收到手指触摸事件
>```Java
>/*** Returns true if a child view can receive pointer events.
>* @hide*/
>private static boolean canViewReceivePointerEvents(@NonNull View child) {
>	return (child.mViewFlags & VISIBILITY_MASK) == VISIBLE 
>|| child.getAnimation() != null;
>}
>```
>当child可见或者当前有动画附加时返回true
>```java
>protected boolean isTransformedTouchPointInView(float x, float y, View child,
>  PointF outLocalPoint) {
>	final float[] point = getTempPoint();
>	point[0] = x;
>	point[1] = y;
>	transformPointToViewLocal(point, child);
>	final boolean isInView = child.pointInView(point[0], point[1]);
>	if (isInView && outLocalPoint != null) {
>		outLocalPoint.set(point[0], point[1]);
>	}
>          
>	return isInView;
>}
>```
>在这个方法里将会判断那些有坐标转换的view是否在手指触控的区域内
>```java
>public void transformPointToViewLocal(float[] point, View child) {
>   	point[0] += mScrollX - child.mLeft;
>   	point[1] += mScrollY - child.mTop;
>
>   	if (!child.hasIdentityMatrix()) {
>       	child.getInverseMatrix().mapPoints(point);
>   	}
>}
>```
>在这个方法中会调用child.hasIdentityMatrix()来判断该view是否有过位移，缩放，旋转之类的属性动画；如果应用过，就会拿到InverseMatrix，然后映射到对应的逆矩阵上；然后判断处理过后点，是否在该View的边界范围内`final boolean isInView = child.pointInView(point[0], point[1]);`
>
>##### 把触摸点映射到该子View的逆矩阵上如何理解？
>
>比如一个View它水平平移了200，那它所对应的逆矩阵就是水平平移了-200，
>如果触摸点坐标是[500,500]的话，那么映射之后，就是[300,500]，也就是反方向移动同样的距离了。
>**或者这样理解**
>如果一个View向右移动了一个拇指的距离，当手指在它的新位置上按下的时候，(它最终还是要判断是否在原来的边界范围内的，那只能把触摸的坐标，给转回去，转回它应用变换之前的位置上)，那ViewGroup在检测到它应用了变换后，会把现在的触摸点，向左(刚刚是向右)移动一个拇指的距离(抵消)，再来判断是否在该View的边界范围内。

##### 那么为什么只有属性动画可以这样，补间动画就不行呢？
>View在draw时候，会检测是否设置了Animation(补间动画)，如果有的话，会获取这个动画当前的值，然后应用到canvas上，把东西draw出来
>
>比如设置了位移动画吗，当前值是向右移动了100px，难么效果等同于这样：
>```java
>Matrix matrix = new Matrix();
>matrix.setTranslationX(100);
>canvas.setMatrix(matrix);
>```
>**它的作用只会在draw的时候有效。**
>
>虽然大家都是操作的Matrix，但是Matrix的对象是不一样的
>**属性动画**操作的Matrix是在View的mRenderNode中的stagingProperties里面的，这里的Matrix，每个View之间都是独立的，所以可以各自保存不同的变换状态，所以在ViewGroup筛选的时候，应用属性动画的View会被正确找到
>**补间动画**操作的Matrix是借用了他**父容器**的一个叫mChildTransformation的属性，通过getChildTransformation获得；也就是说，一个ViewGroup中，无论它有几个子View，这些子View播放补间动画时，都是公共同一个Transformation对象的Matrix，这个对象放在ViewGroup中；所以补间动画做出的变化是直接应用在画布上的，变换了就变化了，没有存值，导致group并不知道儿子的新位置？所以无法反向查找view判断事件分发区域？
>
>**共用？不可能吧，那为什么可以同时播放好几个动画，而互相不受影响呢？**
>
>是的，确实是共用，在补间动画更新每一帧时，父容器的mChildTransformation里面的Matrix，都会被reset
>**每次重置Matrix而不受影响的原因：**是因为这些补间动画，都是基于当前播放进度，来计算出**绝对的动画值**并应用的，保存旧动画值是没有意义的。就拿位移动画TranslateAnimation来说，比如它要向右移动500，当前的播放进度是50%，那就是已经向右移动了250，在View更新帧的时候，就会把这个向右移动了250的Matrix应用到Canvas上，当下次更新帧时，比如进度是60%，那计算出来的偏移量就是300，这时候，已经不需要上一次的旧值250了，就算Matrix在应用前被重置了，也不影响最后的效果。

