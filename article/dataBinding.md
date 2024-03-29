# 2019.9.4
DataBinding的优缺点？

#### 前言
> DataBinding框架并没有用其它高级的Api替代现有的Api，只是对原有Api的封装，也就是说不管是findViewById()，还是setText，click事件，DataBinding还是用findViewById这些原有Api实现的，只是把它隐藏起来了，我们开发过程中不用自己写，因为框架帮我们写了
> 

#### 起源
>DataBinding是谷歌官方发布的一个框架(现在属于Jetpack组件)，基于页面数据直接绑定额MVVM框架，其可以直接在xml文件中给控件绑定数据，通过Binding类(和XML文件绑定)直接拿到有id的控件，页面对数据的监听可以直接修改数据就能改变页面的数据，即使页面有多处使用到(LiveData功能)；可以简绍代码中频繁出现的findViewById()问题
>```xml
><layout>
>  <!-- 数据功能区-->
>  <data>
>		<!-- 导包-->                     
>		<import type="xxxx"/>
>		<import type="xxxx"/>                      
>		
>		<!-- 定义变量-->                      
>  	<variable
>			name = "user"
>			type = "xxxx"/>     
>  </data>
>  
>  <!-- 布局功能区-->
>  <androidx.constraintlayout.widget.ConstraintLayout>
>		//各种布局控件,通过数据功能区的数据绑定到控件
>  </androidx.constraintlayout.widget.ConstraintLayout>
></layout>
>```
>

#### 工作原理
###### 1.改造XML
> 我们在编写XML文件的时候，会像上面一样，需要在头部加上layout标签，layout标签中的元素由两部分组成，`data` 和 `根布局(如LinearLayout)`；其实这种xml文件写法只是一种形式，并不是什么高级Api，这个xml文件在编译的时候会被改造；编译后生成的xml文件在 `build\intermediates\data-binding-layout-out\debug` 目录下，通过对比我们发现，我们手动添加的layout，data，以及@{xxx}这些看似高级的Api用法，其实在编译后都去掉了，取代它们的是在各个绑定了@{xxx}的View添加了一个Tag，这些Tag以binding_开头，后面接一个数字；要注意，没有绑定@{xxx}的View不会添加Tag，然后在根布局里也添加了一个Tag，名字是 `layout/xxx_xxx`
> 

###### 2.绑定layout
> 绑定layout的代码有两种，Activity里是`DataBindingUtil.setContentView()`,Fragment里是 `DataBindingUtil.inflate()`，两个方法调用后都会走到bind()这个方法，后面会通过switch判断layoutId，然后调用对应layout的xxxBinding类的bind方法；这个类是自动生成的，是对应layout的名字转成驼峰标识后加Binding后缀，比如你的layout名字为activity_main.xml，则对应的Binding类的名字为ActivityMainBinding；在这个类的实现类ActivityMainBindingImpl的构造方法中会去绑定View和Tag
> 

#### 使用中存在的问题
###### 1.极难进行错误定位
> 通常出现错误的都是XML文件；一般来说真正的错误都在build日志最后看到，有的时候还需要在控制台输入`./gradlew build --stacktrace` 或 `gradlew assembleDebug --stacktrace --debug` 或 `gradlew assembleDebug --info` 来查看详细的报错日志；如果实在找不到错误目标，可以手动查看改动的XML文件,95%的错误都出自XML文件的
>

###### 2.代码极难维护
> 由于DataBinding的双向绑定问题，我们会在XML文件中做一些简单的逻辑处理，这样的操作会让我们的代码变得相对简洁，并且可以省去findViewById()带来的性能损耗。但是这样的操作会让后续功能迭代变得很痛苦，如果一个页面比较复杂，就会涉及到许多XML文件，让后续的维护难度加大；通常我们需要将复杂的逻辑放在代码中去处理，这样后续的改动就会灵活许多
>

###### 3.@{}中的内容不会去做检查
> XML中的控件支持用类似 `@{xxx}`的方式绑定数据，但是在XML里并没有检测机制，所以极易出现原来你是一个`Number`类型的值，编译器却当做`resourceId`进行处理而报错
> 

###### 4.根据控件id在代码里找一个东西很难
> 如果在你的项目中XML文件里的控件命名方式为`xxx_xxx_xxx`的话，在代码中你根据这个id是找不到这个控件的，因为DataBinding里面为我们生成变量采用的是驼峰命名法
> 

###### 5.部分XML中的表达式在不同的gradle版本上表现有所不同
> 如果在你的项目中出现了这样一种情况：同样的代码，你本地代码可以编译运行，但是在服务器或者其他同事的电脑上编译报错，这时候你可以检查一下gradle版本是否一致
> 

###### 6.BindingAdapter不好维护
> 通常我们会使用@BindingAdapter来做一些共用逻辑，而不是直接把逻辑放在页面通过设置属性来使用它，这样就会出现公用逻辑比较难维护
> 

###### 7.多模块依赖问题
> DataBinding在多模块开发的时候有这样一个机制：
> **1.** 如果子模块使用了DataBinding，那么主模块也必须在gradle上加配置，不然就会报错
> **2.** 如果主模块和子模块都添加上DataBinding的配置，那么在编译时，子模块的XML文件产生的Binding类除了在自己的build里会有一份外，在主模块下也会有一份
>
> 那么，如果主模块和子模块都有一个activity_main.xml文件，主模块生成的ActivityMainBinding会是根据子模块的文件生成的！这种情况我们还可以通过让主模块和子模块使用不同的命名解决，那么下面这种情况就很难解决了：如果子模块的某个xml文件使用了一些第三方控件，那么主模块由于也会生成这个文件的Binding类，并且会有第三方控件的引用，这时候由于主模块没有引入这些控件，就会报错，虽然可以通过api的方式引用第三方控件，但是这样就违背了解耦的原则，使用多模块开发的初衷
