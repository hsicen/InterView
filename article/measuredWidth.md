# 2019.8.31
1. getWidth,getMeasuredWidth 有什么区别?
>**getMeasuredWidth** 返回的是**View根据自身内容测量**得到的宽度，这个宽度是在setMeasureDimension方法被调用后刷新的(在onMeasure中调用)，这个方法获取的宽度是可能会变化的，不是最终的宽度
>
>**getWidth** 返回的是**View根据父View的总体情况**最终确定的宽度(layout中确定的)，在View代码中返回的是**mRight-mLeft**，这两个值是在setFrame方法被调用后赋值的(**layout()**最终会调用**setFrame()**)
>
>大多数情况下，这两个方法返回的值是一样的

2. 传说中一个是View的宽度，一个是View中内容的宽度，这个解答对么？
> 如果该View及其ViewGroup按照正常的测量和布局流程，getWidth得到的是View的宽度，getMeasuredWidth得到的是View中内容的宽度，而且这两个的值是相等的；**但是**如果在测量或者布局中进行了人为的干预或者在**onLayout()**和**onDraw()**中手动调用了**measure(xx,xx)**，前面的结果可能会不同

3. 如何在onCreate中拿到View的宽度和高度？
> - View.post(runnable)
> - ViewTreeObserver.addOnGlobalLayoutListener(OnGlobalLayoutListener listener)
> -  Activity#onWindowFocusChanged(boolean hasFocus)

