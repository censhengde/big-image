package com.tencent.bigimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_big_image_view.*

class BigImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_image_view)
        try {
            val ins = assets.open("big-image2.jpg")
            big_image_view.setImage(ins)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
