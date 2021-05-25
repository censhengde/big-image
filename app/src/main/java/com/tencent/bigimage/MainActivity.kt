package com.tencent.bigimage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun onClickBtn(view: View) {
        val btn=view as Button
        when(btn.text){
            "big-ImageView" ->{
                startActivity(Intent(this,BigImageViewActivity::class.java))
            }
            "ImageView" ->{
                startActivity(Intent(this,ImageViewActivity::class.java))

            }
        }

    }
}
