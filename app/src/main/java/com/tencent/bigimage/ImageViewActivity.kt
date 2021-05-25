package com.tencent.bigimage

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image_view.*

class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        val ins = assets.open("big-image2.jpg")
        val bitmap = BitmapFactory.decodeStream(ins)
        imageview.setImageBitmap(bitmap)
    }
}
