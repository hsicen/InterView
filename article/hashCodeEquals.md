# hashCode与equals的区别？

#####  两个对象，equals相同，hashCode一定相同么？
> hashCode一定相同

##### hashCode相同，equals一定相同么？

> 不一定相同

##### 二者在什么时候配合？如何配合？在哪些源码中可以看到类似的配合？

> equals和hashCode是Object的两个方法，equals是判断两个对象的各个属性值是否相同，hashCode是计算hash值，是int值，取值有限；
>
> 在HashMap和HashTable中，HashMap中的put方法就是先查看hashCode值，如果存在hashCode值就调用equals看元素是否存在，存在就更新，不存在就插入该值，在插入大量非重复数据时，可以有效减少equals的调用，从而提高效率