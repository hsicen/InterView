# 2019.8.25
哪些 Context调用 startActivity 需要设置NEW_TASK，为什么？
1. 最常见的就是 Application 需要设置 NEW_TASK了，为什么需要呢？
> 在开发中我们直接接触的Context主要有：Application,Activity和Service，他们三者都是间接继承自Context的；Context是一个抽象类，我们可以看它的最终实现类ContextImpl的startActivity,我们可以发现在ContextImpl#startActivity中，会对Intent.flag进行一系列的检查，用于判断把启动的Activity加入到哪个任务栈中，判断的大概意思就是除了Activity(**Activity类覆盖了startActivity方法，允许新启动的Activity和当前Activity在同一个任务栈中**)之外，其它的Context调用startActivity都必须加上**FLAG_ACTIVITY_NEW_TASK**，否者会直接抛出异常；判断后最终会调用Instrumentation.execStartActivity 执行跳转
>
> 比较有意思的是在TargetSDKVersion为24,25,26,27这些版本上，这个判断出现了Bug，也就是在这之间的版本上你可以不设置**FLAG_ACTIVITY_NEW_TASK**也可以利用非Activity的Context进行Activity的跳转,本应该为options == null的判断被写成了options != null,所以没有进入抛出异常的分支判断
>
> 联想到Android的四种启动模式(Standard,SingleTask,SingleTop,SingleInstance)，Activity有一个Activity栈去管理它，如果你用一个非Activity的Context去启动一个Activity的话，新的Activity并不知道自己应该放在哪个Activity任务栈中,而设置上 FLAG_ACTIVITY_NEW_TASK 标记，就会直接创建一个 Activity 栈来管理它了。实际上，这样的启动方式就是以**SingleTask**模式启动的。

2. 其他的 Context 呢？
> BroadcastReceiver和ContentPrider都没有直接或者间接的继承Context,ContentProvider#getContext()得到的是Application的实例，而BroadcastReceiver#onReceiver中的Context参数是发送广播的那个Context实例