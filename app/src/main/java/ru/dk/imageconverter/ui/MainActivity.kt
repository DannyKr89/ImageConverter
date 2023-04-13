package ru.dk.imageconverter.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import ru.dk.imageconverter.data.PictureConverterRepoImpl
import ru.dk.imageconverter.databinding.ActivityMainBinding
import ru.dk.imageconverter.domain.PictureConverterRepo
import ru.dk.imageconverter.ui.mvp.ConvertView
import ru.dk.imageconverter.ui.mvp.ConverterPresenter

class MainActivity : AppCompatActivity(), ConvertView {

    private lateinit var binding: ActivityMainBinding
    private val pictureConverter: PictureConverterRepo = PictureConverterRepoImpl(this.contentResolver)
    private val presenter: ConverterPresenter = ConverterPresenter(pictureConverter)
    private val IMAGE_PICK_CODE = 15465


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter.attachView(this)

        verifyPermission()

        binding.takePicture.setOnClickListener {
            takePicture()
        }

        binding.convertPicture.apply {
            isEnabled = false
            setOnClickListener {
                presenter.convertPicture()
            }
        }

    }

    private fun verifyPermission() {
        val permission = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), 1
            )
        }
    }

    private fun takePicture() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE, null)
    }

    override fun showImage(bitmap: Bitmap) {
        binding.ivPicture.setImageBitmap(bitmap)
    }

    override fun showConverted(bitmap: Bitmap) {
        binding.ivConvertedPicture.setImageBitmap(bitmap)
    }

    override fun showError(throwable: Throwable) {
        Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            binding.convertPicture.isEnabled = true
            if (data?.data != null) {
                presenter.takePicture(data.data!!)
            }
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}