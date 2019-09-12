# 2019.9.2
**ANR的产生的原理是什么，AMS中涉及ANR的代码有哪些？**

>打开ActivityManagerService(SDK28)，在**1891**行我们可以看到定义个一个int类型的常量``` static final int SHOW_NOT_RESPONDING_UI_MSG = 2;```根据意思我们可以猜测，这是标记UI无反应的消息标识；按住Ctrl我们发现在UiHandler中使用了该标记
>```Java
>case SHOW_NOT_RESPONDING_UI_MSG: {
>	mAppErrors.handleShowAnrUi(msg);
>	ensureBootCompleted();
>} break;
>```
>
>通过源码可以发现，在UiHandler收到**SHOW_NOT_RESPONDING_UI_MSG** 时会去调用AppErrors的handleShowAnrUi()方法，在该方法中会去创建AppNotRespondingDialog弹窗，最终会以TYPE_SYSTEM_ERROR的方式弹出
>
>通过以上分析可以确定，ANR弹窗是在AMS中的UiHandler收到标记为**SHOW_NOT_RESPONDING_UI_MSG** 的消息时弹出来的
>
>那么在哪些地方会发出**SHOW_NOT_RESPONDING_UI_MSG**的消息呢？
>点击该消息的引用，会发现在AppErrors的appNotResponding()方法里有引用
>```java
>	// Bring up the infamous App Not Responding dialog
>	Message msg = Message.obtain();
>	msg.what = ActivityManagerService.SHOW_NOT_RESPONDING_UI_MSG;
>	msg.obj = new AppNotRespondingDialog.Data(app, activity, aboveSystem);
>	mService.mUiHandler.sendMessage(msg);
>```
>可以看到，在这个方法里会通过AMS对象的UiHandler发送请求弹出ANR对话框的消息，那么这个方法在哪些地方调用了呢？通过分析源码，我发现一共有一下四处地方调用了该方法：
>- ActiveServices的serviceTimeout方法(**后台服务超时**)
>
>- ActiveServices的serviceForegroundTimeout方法(**前台服务超时**)
>
>- ActivityManagerService的appNotRespondingViaProvider方法(**Provider发出的无响应**)
>
>- ActivityManagerService的inputDispatchingTimedOut方法(**input事件分派的时候超时**KeyEvent或MotionEvent)

**UI线程执行一个非常耗时的操作一定会出现ANR弹框吗？**

>分析inputDispatchingTimedOut()方法的源码发现存在两种情况不会弹出ANR弹窗
>```java
>public boolean inputDispatchingTimedOut(final ProcessRecord proc,final ActivityRecord activity, final ActivityRecord parent,final boolean aboveSystem, String reason) {
>	//事件权限检查
>            
>	//reason参数判断                    
>
>	//debug模式，直接返回                    
>	if (proc.debugging) return false;
>	
>	if (proc.instr != null) {
>		Bundle info = new Bundle();
>		info.putString("shortMsg", >"keyDispatchingTimedOut");
>		info.putString("longMsg", annotation);
>		finishInstrumentationLocked(proc, >Activity.RESULT_CANCELED, info);
>		return true;
>	}
>	
>	mAppErrors.appNotResponding(proc, activity, parent, aboveSystem, annotation);
>	return true;
>}
>```
>第一情况是在应用处于调试模式时,不会弹出ANR弹窗，
>第二种情况是```proc.instr != null```，这种情况下会直接kill掉进程
>
>**那么什么情况下instr会不为空呢？**
>通过分析该变量的引用我们发现，当AMS的attachApplication方法被调用时，该属性是可能被赋值的
>**那什么时候attachApplication方法会被调用呢？**
>其实就是ActivityThread的main方法执行的时候(启动)，它会调用一个attach方法，而attachApplication会在这个attach方法里面被调用
>**instr赋值的条件是什么？**
>赋值条件是mActiveInstrumentation里面不为空。
>**那么什么时候mActiveInstrumentation里面不为空？**
>看AMS代码**7763**行(检查mActiveInstrumentation是否为空那里)可以发现有一句注释：```"Check if this is a secondary process"，```那我们可以知道，这个判断是检测当前进程是否次进程(多进程环境下)，如果是次线程(不是主进程)，那么经过安全检查之后，就会把mActiveInstrumentation里面的实例，赋值给proc的instr。

**总结：**

ANR对话框是AMS收到**SHOW_NOT_RESPONDING_UI_MSG**消息后弹出的，在以下情况下会发出该消息
1. ActiveServices的serviceTimeout方法(**后台服务超时**)
2. ActiveServices的serviceForegroundTimeout方法(**前台服务超时**)
3. ActivityManagerService的appNotRespondingViaProvider方法(**Provider发出的无响应**)
4. ActivityManagerService的inputDispatchingTimedOut方法(**input事件分派的时候超时**KeyEvent或MotionEvent)
在input事件分发超时的时候，有两种情况下不会弹窗：一种是处于debug模式，另一种是来自子进程(这种情况下会直接kill掉子进程)