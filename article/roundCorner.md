# Android 常见的制作圆角方案有哪几种常见方式？ 
> 一开始我们会想到直接用 CardView 或者是编写 Shape 文件来处理；如果用 CardView，这无疑是增加布局层次，Shape 文件一般作为背景处理，抛开同样是增加布局层次以外，假设我们有成千上万个 Shape 文件，如何维护？
>
> 所以，换一种思路，直接在项目里面写一个自定义 View和自定义 ViewGroup来处理圆角或者是渐变或者是纯色背景，又或者是圆角边线的问题。我们可以在代码中通过自己自定义的属性来处理各种各样的 Shape 属性问题，比如 **GradientDrawable** 来处理 shapeMode。对于渐变色，**GradientDrawable#Orientation** 来处理渐变，我们甚至可以使用**RippleDrawable**来实现5.0系统以上的水波纹效果。
>
> RoundedBitmapDrawableFactory
> BitmapShader