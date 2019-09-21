# 2019.8.22
app or lib module 的R.java文件变量final修饰符？

1. 为什么App Module中的R.java文件的变量是final修饰而Lib Module中R.java文件却不是？
> R文件是由编译器自动生成，每个模块中的R文件的id都是从0x7f+resId+0001开始分配的，所以说多个模块肯定会有资源冲突的(同名资源文件)，其实lib module应该是没有R.java文件的，只是as的一个语法支持，在编译成apk时，会替换每个资源文件的id为具体的数值，而lib的资源文件的id是会变的，故不能用final进行修饰

2. 为什么将App Module中的switch-case语句拷贝到Lib Module中需要转换成if-else？
> 由1可知，lib module中的资源文件id是可变的，而在Java语法中，switch的参数必须是常量或者值，否则会报语法错误，只需要修改成if-else即可解决
