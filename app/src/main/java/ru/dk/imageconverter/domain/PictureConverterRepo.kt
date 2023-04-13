package ru.dk.imageconverter.domain

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface PictureConverterRepo {
    fun convertPicture(bitmap: Bitmap) : Single<Bitmap>
    fun savePicture(bitmap: Bitmap) : Completable
    fun openPicture(uri: Uri, contentResolver: ContentResolver) : Single<Bitmap>
}