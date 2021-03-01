package com.shesha.kotlinprojects.imageselectorlibrary

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.shesha.libraries.customimagelibrary.ImageSelector
import java.io.File


class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    val authority = "com.shesha.kotlinprojects.imageselectorlibrary"
    var crop: Boolean = false
    private lateinit var button: Button
    private lateinit var imageView : ImageView

    private val PERMISSION_REQUEST_CODE: Int = 101

    private var mCurrentPhotoPath: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.select_image_button)
        imageView = findViewById(R.id.avatar_image)
        button.setOnClickListener {
            if (checkPersmission()){
                crop = true
                ImageSelector.setCurrentPath(mCurrentPhotoPath)
                ImageSelector.selectImage(this, packageName)
                // SelectImage.CropTheImage(this)
            } else requestPermission()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {

                    Log.d("Mytah","permission")

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    ImageSelector.cropImage(this)
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else if (requestCode ==3)
            {
                mCurrentPhotoPath = ImageSelector.mCurrentPhotoPath
                val auxFile = File(mCurrentPhotoPath)
                var bitmap : Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
                imageView.setImageBitmap(bitmap)
            }
            else if (requestCode == 2) {
                val selectedImage = data!!.data
                val filePath = arrayOf(MediaStore.Images.Media.DATA)
                val c: Cursor? = contentResolver.query(selectedImage!!, filePath, null, null, null)
                c!!.moveToFirst()
                val columnIndex: Int = c.getColumnIndex(filePath[0])
                val picturePath: String = c.getString(columnIndex)
                c.close()
                val thumbnail = BitmapFactory.decodeFile(picturePath)
                val uri = FileProvider.getUriForFile(this,authority,File(picturePath))
                ImageSelector.cropImage(this,uri2 = uri)
                //  imageView.setImageBitmap(thumbnail)
            }
        }
    }


    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

}