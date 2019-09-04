# 2019.8.29
在Activity的onResume方法中handler.post(Runnable)能获取到View宽高吗？

>**不能**获取到View的宽高
>
>View的绘制流程如下(包含Activity的启动过程)
>
>- 在IApplicationThread收到启动Activity的消息时，在performLaunchActivity中通过Instrumentation#newActivity创建Activity，PhoneWindow以及对应的DecorView
>- 接着执行handleResumeActivity#performResumeActivity，在handleResumeActivity中处理Activity的生命周期并通过WM添加Activity的Window，同时创建一个ViewRootImpl与DecorView绑定。在ViewRootImpl与DecorView绑定后，ViewRootImpl发送同步障碍消息，然后通过Choreographer发送异步消息，等待下一个VSYNC到来时执行performTraverse开始绘制
>
>**onResume与ViewRootImpl与DecordView绑定两者顺序问题**
>
>- performResumeActivity是在handleResumeActivity中被调用
>- 生命周期方法onResume是在performResumeActivity中触发
>- ViewRootImpl与DecordView绑定是在handleResumeActivity中，在performResumeActivity之后
>
>**因此可以知道onResume在ViewRootImpl与DecordView绑定之前执行**
>
>#### onResume中调用handler.postRunnable的消息是否在异步消息之前执行？
>
>- Handler收到同步障碍消息后（target = null的消息）会优先执行异步消息
>- handler.postRunnable的消息何时会执行？当前面的同步消息都执行完了，轮到它的时候，它就会被执行
>- 因此handler.postRunnable可能在异步消息前执行完毕，也可能由于阻塞导致handler.postRunnable的消息在异步消息来之前都还没执行，就会在异步消息后执行
>
>##### 总结：在Activity的onResume方法中通过handler.postRunnable获取View宽高是不可靠的

