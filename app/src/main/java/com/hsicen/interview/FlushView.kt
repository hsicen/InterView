package com.hsicen.interview

import android.content.Context
import android.graphics.Canvas
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * <p>作者：Hsicen  2019/8/21 8:44
 * <p>邮箱：codinghuang@163.com
 * <p>作用：
 * <p>描述：屏幕刷新流程分析
 */
class FlushView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var lastFlashTime = SystemClock.uptimeMillis()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d("flash", "${SystemClock.uptimeMillis() - lastFlashTime}")
        lastFlashTime = SystemClock.uptimeMillis()
        invalidate()
    }
}