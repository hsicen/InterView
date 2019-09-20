package com.hsicen.databinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hsicen.databinding.bean.Student
import com.hsicen.databinding.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        mainBinding.student = Student("张三", "21")

        tvAge.context
    }
}
