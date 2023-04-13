package ru.dk.imageconverter.data

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.dk.imageconverter.domain.PictureConverterRepo
import java.io.*
import java.util.*

class PictureConverterRepoImpl(private val contentResolver: ContentResolver) : PictureConverterRepo {

    override fun convertPicture(bitmap: Bitmap) = Single.create<Bitmap> {
        val stream = ByteArrayOutputStream()
        if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
            val byteArray = stream.toByteArray()
            val convertedPicture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            it.onSuccess(convertedPicture)
        } else {
            it.onError(Throwable("Ошибка конвертации"))
        }
    }.subscribeOn(Schedulers.io())


    override fun savePicture(bitmap: Bitmap) = Completable.create {
        val path = Environment.getExternalStorageDirectory().toString()
        val file = File(path, "${UUID.randomUUID()}.png")
        val stream: OutputStream = FileOutputStream(file)
        if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
            stream.flush()
            stream.close()
            it.onComplete()
        } else {
            it.onError(Throwable("Ошибка сохранения"))
        }
    }.subscribeOn(Schedulers.io())

    override fun openPicture(uri: Uri) = Single.create<Bitmap>{
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        it.onSuccess(bitmap)
    }.subscribeOn(Schedulers.io())

}