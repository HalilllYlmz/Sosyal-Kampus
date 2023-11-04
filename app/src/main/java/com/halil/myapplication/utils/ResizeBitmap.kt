package com.halil.myapplication.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class ResizeBitmap {


    fun smallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        val resizedBitmap = Bitmap.createScaledBitmap(image, width, height, true)

        return resizedBitmap
    }

    fun bitmapToByteArray(resizedBitmap: Bitmap) : ByteArray{
        val baos = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }


}