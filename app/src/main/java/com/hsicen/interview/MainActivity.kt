package com.hsicen.interview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * <p>作者：Hsicen  2019/8/16 13:55
 * <p>邮箱：codinghuang@163.com
 * <p>功能：
 * <p>描述：每日一问
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BuildConfig.BUILD_TYPE

        "1".hashCode()
    }

    override fun onResume() {
        super.onResume()

        Log.d("hsc", Thread.currentThread().name)
        tvHello.post {
            Log.d("hsc", "${tvHello.width} * ${tvHello.height}")
            Log.d("hsc", Thread.currentThread().name)
        }
    }
}
