# Android16.6ms刷新一次屏幕？

1. 16.6ms刷新一次屏幕是什么意思呢？
> 是指在界面显示 UI 更新的时侯为了提供流畅的体验效果，需要每隔 16.6ms 对屏幕上的 UI 元素进行重绘，如果重绘时间大于 16.6ms UI 界面就会产产生卡顿。

2. 每隔16.6ms都会调用一次View的onDraw么？
> 不是的，这个频率，说的是系统发出屏幕刷新信号频率，但是在onDraw中收到回调的时机，是不确定的（各种原因，如代码写法、设备性能等）。

3. 如果不是？那是什么样的一个刷新机制？
> **屏幕刷新流程**
> 1. View调用invalidate()方法
> 2. ViewRootImpl会把doTraversal(处理View的测量，布局，绘制)任务post到Choreographer中(在系统下一次发出同步信号的时候，这个doTraversal任务会被执行)
> 3. Choreographer会借助DisplayEventReceiver的scheduleVsync方法，在Native层(IDisplayEventConnection.cpp)通过Binder(跨进程)发出一个REQUEST_NEXT_VSYNC的Tag
> 4. 在DisplayEventDispatcher.cpp中，会看到一个handleEvent方法(可以猜测，系统在下一次发出屏幕刷新信号时，间接或直接回调的就是这个方法)，它里面会调用android_view_DisplayEventReceiver.cpp中的dispatchVsync方法
> 5. 回到java层，在DisplayEventReceiver中会看到一个同签名的dispatchVsync方法，并且上面有注释写着：Called from native code.可以知道这个方法是在Native层被调用的，它里面会调用onVsync方法
> 6. 而onVsync方法在Choreographer中内部类的实现，最终是会调用doFrame方法的，这个doFrame方法，里面会把刚刚在ViewRootImpl中post到Choreographer里的doTraversal任务执行！！！

4. 这个机制在Android版本迭代中有无变化？
> 这个机制是有变化的，经过测试发现在6.0的系统上，是有缓存机制的，不断的invalidate，每两次中是有一次是 <= 1ms就回调了，可以猜测这一次是没有等待系统的屏幕刷新信号就直接回调了onDraw()；而9.0的系统比较稳定，onDraw()每一次被回调的间隔都是15ms左右