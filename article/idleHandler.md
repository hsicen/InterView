# 2019.8.23
什么是IdleHandler？

1. 它有什么能力和作用?
> 查看MessageQueue中的IdleHandler源码发现，当消息队列空闲时会执行IdleHandler的queueIdle()方法，该方法返回一个boolean值，如果为false则执行完毕之后移除这条消息，如果为true则保留，等到下次空闲时会再次执行，查看MessageQueue的next()方法可以发现确实是这样
>
> 处理完IdleHandler后会将nextPollTimeoutMillis设置为0，也就是不阻塞消息队列，当然要注意这里执行的代码同样不能太耗时，因为它是同步执行的，如果太耗时肯定会影响后面的message执行。
>
> 能力大概就是上面讲的那样，那么能力决定用处，用处从本质上讲就是趁着消息队列空闲的时候干点事情，当然具体的用处还是要看具体的处理。

2. 有什么适用的场景?
>要使用IdleHandler只需要调用MessageQueue#addIdleHandler(IdleHandler handler)方法即可
>
>合适场景可以从以下一点或几点出发
>- 消息队列相关
>- 主线程能干的事情
>- 返回true和false带来的不同结果
>
>目前可以想到的场景
>1. Activity启动优化：onCreate，onStart，onResume中耗时较短但非必要的代码可以放到IdleHandler中执行，减少启动时间
>2. 想要在一个View绘制完成之后添加其他依赖于这个View的View，当然这个用View#post()也能实现，区别就是前者会在消息队列空闲时执行
>3. 发送一个返回true的IdleHandler，在里面让某个View不停闪烁，这样当用户发呆时就可以诱导用户点击这个View，这也是种很酷的操作
>4. 一些第三方库中有使用，比如LeakCanary，Glide中有使用到