# Android消息机制和应用

- ThreadLocal
> 是用来存储指定线程的数据的，当某些数据的作用域是该指定线程并且该数据需要贯穿该线程的所有执行过程时就可以使用ThreadLocal存储数据，当某线程使用ThreadLocal存储数据后，只有该线程可以读取到存储的数据，除此线程之外的其他线程是没办法读取到该数据的。
>```Java
> ThreadLocal<Boolean> local = new ThreadLocal<>();
> local.set(true);
>Boolean bool = local.get(); //获取到 true
> 
> new Thread() {
> @Override
>  public void run() {
>         Boolean bool = local.get(); //获取到 null
>         local.set(false);
>     }
>    }.start():
>
>    Thread.sleep(1000);
>    Boolean newBool = local.get(); //获取到 true
>    ```
> 就算是同一个`ThreadLocal`对象，任一线程对其的`set()`和`get()`方法的操作都是相互独立互不影响的。

- Handler  Looper  MessageQueue
> `Handler`和`Looper`组成了一个生产者消费者模式，`Handler`作为生产者向`MessageQueue`添加产物`Message`，`Looper`作为消费者，在`Looper#loop()`方法的死循环中从`MessageQueue#next()`循环取出`Message`进行消费。

> `MessageQueue`采用的是单向链表数据结构，`mMessage`是链表的第一个元素，`Message`的`next`字段保存链表的下一个元素。
>
> MessageQueue#next()方法，next()里面有一个for(;;)循环，循环体内调用了nativePollOnce(long, int)方法，这是一个Native方法，实际作用是通过Native层的MessageQueue阻塞当前调用栈线程nextPollTimeMls毫秒的时间。
> **nextPollTimeMls**取值的不同情况的阻塞表现：
> 小于0，一直阻塞，直到被唤醒
> 等于0，不会阻塞
> 大于0，最长阻塞nextPollTimeMls毫秒，期间如被唤醒会立即返回 
>
> `MessageQueue`中有一个`nativeWake(long)`的Native方法，可以唤醒`nativePollOnce()`的阻塞。
>
> 循环开始前nextPollTimeMls的值是0，那么nativePollOnce()方法将会立刻返回，此时尝试取出下一个Message元素，如果没有下一个元素，nextPollTimeMls的值被修改为-1，此时nativePollOnce()进入阻塞状态，等待下一个Message的进入并唤醒阻塞，然后取出Message对象返回。

>在Looper的构造方法中，初始化了Looper的MessageQueue对象；初始化Looper和获取Looper的方法使用到了ThreadLocal，在ThreadLocal中我们介绍了ThreadLocal#get()只能获取到当前线程保存的数据;在Looper#loop()方法中首先判断了当前线程的Looper是否为空，为空就抛出运行时异常，中断当前操作;不为空则进入死循环读取消息队列中的消息，把消息发回发送消息的Handler去分发。

>当某个线程要使用Android的Handler消息机制时，首先要调用Looper#prepare()静态方法为当前线程生成一个Looper对象，紧接着调用Looper#loop()静态方法后，会拿出该线程的Looper对象的MessageQueue开始循环调用MessageQueue#next()方法获取消息队列的下一个Message并处理。
>
>当MessageQueue中没有下一个Message时，next()方法会调用MessageQueue#nativePollOnce()阻塞当前线程，直到下一个Message被加入并通过MessageQueue#nativeWake()唤醒阻塞，此时便可以拿出下一个Message返回给Looper，Looper通过msg.target.dispatchMessage(msg)分发消息。

>在Handler#dispatchMessage(Message)中，首先判断了该Message是否是Runnable，如果是，则直接执行Runnable#run()方法，如果不是则看当前Handler是否有Callback对象，如果有的话就回调到Callback#handleMessage(Message)方法去，如果没有则调用Handler#handleMessage(Message)方法。

>**子线程为什么不能直接new Handler**
>
>到调用new Handler()时会判断Looper.myLooper()方法获取当前线程的Looper，如果为空则会抛出运行时异常中断当前线程，不为空则拿当前线程的Looper对象中的MessageQueue对象，等待Handler#sendMessage()等方法向消息队列中添加消息。
>
>因此，在子线程中直接`new Handler()`时，当前子线程的`Looper`对象势必为空，为空则不能继续消费`Handler`产生的`Message`了，自然得抛出一个异常。

>**Handler线程切换**
>
>当某个线程要使用Android的消息机制时，首先必须要调用Looper#prepare()方法为当前线程生成一个Looper对象，然后在该线程中调用Looper#loop()拿出该线程的Looper对象的MessageQueue开始循环处理其中的消息，如果消息队列为空，那么该线程就会被MessageQueue#nativePollOnce()阻塞起来，只要该队列中进来消息时，该线程同时被MessageQueue#nativeWake()唤醒。其他线程要向该线程发送消息时，只要拿到该线程的Looper并在其他线程实例化Handler，在其他线程中使用Handler发送消息即可向该线程的MessageQueue中添加一个消息，此时该线程的Looper#loop()方法即可获取到消息并在该线程中处理了。

> **Looper.loop()死循环为什么不会导致主线程发生ANR？**
>
> `Looper.loop()`中的死循环和阻塞保证了主线程一直在运行，而不是挂掉，它运行过程就是主线程的运行过程，因此`Looper.loop()`中的死循环和`MessageQueue#nativePollOnce()`不会导致主线程发生ANR。
>
> 主线程内MessageQueue#nativePollOnce()一直阻塞，是否会特别消耗CPU资源呢？这里其实是利用了Linux pipe/epoll机制，当MessageQueue#nativePollOnce()阻塞时，此时主线程会释放CPU资源进入休眠状态，直到下一条消息被加入消息队列，并调用MessageQueue#nativeWake()后，通过往pipe管道写端写入数据来唤醒主线程工作。因此主线程在阻塞时，其实是处于休眠状态，并不会消耗大量CPU资源。

>**ANR是如何发生的？**
>
>Android所有的UI操作都通过`Handler`来发消息操作的，包括屏幕刷新，各种点击事件，Activity的生命周期等。因此当`Looper.loop()`取到任一消息后，处理该消息的时间过长，影响到屏幕刷新速率，此时造成UI卡顿现象，乃至发生ANR。
>**在子线程中向主线程发送一条消息**
>
>```Java
>private Handler mHandler = new Handler(Looper.getMainLooper());
>mHandler.post(new Runnable(){
>... //此处运行在主线程
>});
>```

- Handler同步屏障机制
> **同步屏障机制是什么？**
>
> 消息机制中有一个重要的类MessageQueue，就是消息队列的意思；在一般情况下，MessageQueue对于当前线程是同步的，那么什么是当前线程呢？就是实例化MessageQueue的线程，在消息机制这个完整的机制中，MessageQueue是在Looper的构造方法中被实例化。也就是说，MessageQueue正常情况是同步处理消息的，明白这一点就可以让同步屏障入场了。
>
> 同步屏障，看字面意思也能猜出个八九分，就是阻碍队列中同步消息的屏障，那么它是如何运行的呢？此时需要引进异步消息，在正常时候，我们发送的Message全都是同步消息，发送异步消息有两种方式，下面分别来看一下。
> **第一种** 使用Handler包含async参数的构造方法
> ```Java
> public Handler(boolean async) {
> this(null, async);
> }
> 
> Handler mHandler = new Handler(true);
> ...
> Message msg = mHandler.obtainMessage(...);
> mHandler.sendMessageAtTime(msg, dueTime);
> ```
> 只要async参数为true，所有的消息都将是异步消息。
>
> **第二种**   显示设置Message为异步消息
> ```Java
> Message msg = mHandler.obtainMessage(...);
> msg.setAsynchronous(true);
> mHandler.sendToTarget();
> ```
> 我们知道，上面的Message的target都是非空的，而在MessageQueue#next()方法中，target非空的Message都会被正常处理（下面会有相关代码），因此在这个时候同步消息和异步消息并没有什么不同。如果同学们思路没有断的话，应该能想到，此时同步障碍就需要登场了。
>
> 如果想让异步消息起作用，就得开启同步障碍，同步障碍会阻碍同步消息，只允许通过异步消息，如果队列中没有异步消息，此时的loop()方法将被Linux epoll机制所阻塞。
>
> 开启同步障碍也很简单，调用MessageQueue#postSyncBarrier()方法即可，因为MessageQueue绑定在Looper上，而Looper依附在Handler上，所以正常情况下，源码中是这样开启同步障碍的：```mHandler.getLooper().getQueue().postSyncBarrier();```  在postSyncBarrier()中实例化了一个Target为null的Message对象，然后插入了链表表头位置；也就是说，当在消息队列中放入一个target为空的Message的时候，当前Handler的这一套消息机制就开启了同步阻断。
>
> 当开启同步障碍后，它是如何生效的呢？分析Looper#loop()得知，最终还是调用了MessageQueue#next()来获取消息队列中的消息；分析next()源码可以看出，当开启了同步障碍时，Looper在获取下一个要执行的消息时，会在链表中寻找第一个要执行的异步消息，如果没有找到异步消息，就让当前线程沉睡。
>
> 
>
> **同步屏障机制的应用**
>
> 在分析View的绘制流程时发现，在ViewRootImpl.java中有使用到这种机制；ViewRootImpl中使用的是主线程的Looper，因此这里会阻断主线程Looper的其他同步消息，在ViewRootImpl和Choreographer中多次使用到了异步消息，以完成View的整个绘制流程。
>
> 当我们点击页面的某个控件时，希望瞬间得到它的回应，而不是卡在那里，最起码有个圈圈在转也行。当我们点击某个按钮，此时开启了一个Activity，如果队列中此时有很多消息在排队等候？那么这个Activity的测量、布局和绘制就得一直等到所有消息被处理完成才能执行，此时我们会看到页面一直黑着或者一直白着，不是我们想要的效果，因此如果这个消息队列有一个优先级的特点，那么不就可以解决这个问题了吗？
>
> 综上，所以在消息机制中很巧妙的融入了优先级特点，这个同步障碍机制，实质上是一个对消息队列的优先级显示。

