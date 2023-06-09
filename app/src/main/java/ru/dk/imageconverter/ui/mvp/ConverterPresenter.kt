package ru.dk.imageconverter.ui.mvp

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import ru.dk.imageconverter.domain.PictureConverterRepo

class ConverterPresenter(private val pictureConverter: PictureConverterRepo) {
    private var view: ConvertView? = null
    private var picture: Bitmap? = null
    private val disposables = CompositeDisposable()


    fun attachView(convertView: ConvertView) {
        view = convertView
    }

    fun detachView() {
        view = null
        disposables.clear()
    }

    fun takePicture(uri: Uri) {
        val takePicture = pictureConverter.openPicture(uri)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    picture = it
                    view?.showImage(picture!!)
                },
                onError = {
                    view?.showError(it)
                }
            )
        disposables.add(takePicture)
    }

    fun convertPicture() {
        val convertPicture = pictureConverter.convertPicture(picture!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    view?.showConverted(it)
                },
                onError = {
                    view?.showError(it)
                }
            )
        val savePicture = pictureConverter.savePicture(picture!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    view?.showError(it)
                },
                onComplete = {

                }
            )
        disposables.addAll(convertPicture, savePicture)
    }
}