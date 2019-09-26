# 在Activity的onResume方法中view.postRunnable能获取到View宽高吗？
>在Activity的onResume方法中view的postRunnable是能够获取到View的宽高的
>
>看下View.post源码(SDK28)
>```java
>public boolean post(Runnable action) {
>    final AttachInfo attachInfo = mAttachInfo;
>    if (attachInfo != null) {
>        return attachInfo.mHandler.post(action);
>    }
>    // Postpone the runnable until we know on which thread it needs to run.
>    // Assume that the runnable will be successfully placed after attach.
>    getRunQueue().post(action);
>    return true;
>}
>```
>当attachInfo不为空时，会调用attachInfo里的Handler的post()方法，如果为空就会调用getRunQueue方法返回当前View的Queen，并调用其post()方法
>
>getRunQueue()返回的是mRunQueue，mRunQueue的解释是**Queue of pending runnables. Used to postpone calls to post() until this view is attached and has a handler.** 意思是View在attach之前(dispatchAttachedToWindow被调用之前)，添加到mRunQueue的Runnable都会被挂起，直到这个View被attach之后才会执行
>
>那么View什么时候被attach呢？	**在ViewRootImpl的performTraversals方法第一次被调用的时候。**
>
>查看ViewRootImpl源码会发现，RootView的layout方法是在ViewRootImpl的**performLayout**方法里调用的，但是**dispatchAttachedToWindow**方法是在**performLayout**方法之前调用的，也就是说dispatchAttachedToWindow回调的时候，View是还没有layout的，那为什么会获取到View的宽高呢？
>
>确实，在dispatchAttachedToWindow回调的时候，View还没有被layout；**但是**不要忘了刚刚调用的View#post时传进去的Runnable，最终也是会作为Handler的postDelay方法的参数被放进MessageQueen中；也是就是说这个Runnable并没有被立即执行，而是在MessageQueen中排队。
>
>最重要的是，本次执行performTraversals方法的task，它也是从MessageQueen中取出来的！这就代表着必须等待这个task执行完毕(performLayout也就执行完毕了)之后，才会轮到下一个。

