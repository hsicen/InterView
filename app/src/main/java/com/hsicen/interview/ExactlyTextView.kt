package com.hsicen.interview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView

/**
 * <p>作者：Hsicen  2019/9/6 8:53
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：InterView
 */
class ExactlyTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Log.d("hsc", "measured:    $measuredWidth * $measuredHeight")
        super.onLayout(changed, left, top, right, bottom)
        Log.d("hsc", "exactly:    $width * $height")
    }
}