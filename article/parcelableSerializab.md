# 为什么Parcelable效率比Serializable高

###  为什么Parcelable效率比Serializable高
##### 设计目的
**Serializable** 是Java API，是一个通用的序列化机制，通过将文件保存到本地或网络流来实现数据的传递，这种数据传递方式不仅可以在单个程序中进行，也可以在两个不同的程序中进行
**Parcelable** 是Android SDK API，是为了在同一个程序的不同组件之间或不同程序(AIDL)之间高效的传递数据，是通过IBinder通信的消息载体，从设计目的上可以看出Parcelable就是为了Android高效传递数据而生的

##### 实现原理
**Serializable** 是通过I/O读写存储在磁盘上的，使用反射机制(序列化的时候没有使用反射)，序列化过程较慢，且在序列化过程中创建了许多临时对象，容易触发GC
**Parcelable** 是直接进行内存拷贝的，自己实现封送和解封(marshalled & unmarshalled)操作，将一个完整的对象分解成Intent所支持的数据类型，不需要反射，所以Parcelable具有效率高和内存开销小的优点

###  Parcelable为了效率损失了什么？
Serializable 是通用的序列化机制，将数据存储在磁盘，可以做到持久化存储，文件的生命周期不受程序影响；Parcelable 的系列化操作完全由底层实现，不同的Android版本实现方式可能不同，所以不能进行持久化存储

###  一个对象可以序列化的关键？
序列化是将一个对象由存储态转化成传输态的过程，把对象转化成字节序列，该字节序列包括该对象的数据、对象的类型信息和存储在对象中数据的类型
在序列化时，对象的各属性都必须是可序列化的，声明为static和transient类型的成员数据不能被序列化，并非所有的对象都可以序列化，至于为什么不可以，有很多原因，比如：
**1.安全方面原因**  比如一个对象拥有private，public等field，对于一个要传输的对象，比如写到文件，或者进行rmi传输等等，在序列化进行传输的过程中，这个对象的private等域是不受保护的
**2.资源分配方面原因**  比如socket，thread类，如果可以序列化，进行传输或者保存，也无法对他们进行重新的资源分配，而且，也是没有必要这样实现 

