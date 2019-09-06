# 2019.8.30

对于代码中有大量的 if/else 你有什么优化思路？

>- **策略模式**，将if代码块的方法根据需要封装成类的action方法，然后用工厂模式创建对应对象，使用对应方法
>- 和事件传递机制一样，采用**责任链模式**一层层传递
>采用多态，创建不同的负责人，每个负责人处理符合自己处理条件的问题，如果不符合自己的处理条件，则把问题移交自己的下一个负责人处理
>- 将else代码放在函数前面，**尽早返回**
>- 把一部分的if else，封装成方法(不能减少嵌套)
>
>
>尽早返回示例
>```Java
>public bool HasAccess(User user, Permission permission, IEnumerable<Permission> exemptions){
>    if (user == null || permission == null)
>        return false;
>    if (exemptions.Contains(permission))
>        eturn true;
>    return SecurityChecker.CheckPermission(user, permission);
>}
>```
>责任链处理示例
>
>![processChain](C:\Android\Code\InterView\image\processChain.jpg)

