# 2019.8.19
大家应该都有泛型在编译期会被擦除的概念，那么为什么我们在运行时还能读取到呢？
> 原因是因为编译器帮我们完成了自动类型转换，因为类型擦除的问题，泛型类型变量最后得到的都是原始类型; Java自动的帮我们进行了一个类型转换checkcast指令,更通俗的理解就是，泛型进入的地方因为类型擦除，默认转型成为了Object对象，然后再泛型离开的地方，编译器为我们自动加上了将Object转型成Target对象的指令。

> 其实在泛型擦除时并不会将所有的泛型类型都擦除掉,它只会擦除运行时的泛型类型，编译时类中定义的泛型类型是不会被擦除的，对应的泛型类型会被保存在Signature中。
> 我们如果想获取对应对象中的泛型类型只需将动态创建的对象改为匿名内部类即可获取，因为内部类实在编译时创建的，泛型类型是会保存下来的
> 对应API **getGeneric...**都是获取泛型类型的
> 泛型的擦除机制实际上擦除的是除结构化信息外的所有东西（结构化信息指与类结构相关的信息，而不是与程序执行流程有关的，即与类及其字段和方法的类型参数相关的元数据都会被保留下来通过反射获取到）。

