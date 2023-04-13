package ru.dk.imageconverter.ui.mvp

import android.graphics.Bitmap

interface ConvertView {
    fun showImage(bitmap: Bitmap)
    fun showConverted(bitmap: Bitmap)
    fun showError(throwable: Throwable)
}