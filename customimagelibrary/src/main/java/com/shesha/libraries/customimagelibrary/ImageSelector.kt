package com.shesha.libraries.customimagelibrary

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object ImageSelector : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    var uri :Uri? = null

    lateinit var bitmap: Bitmap

    var mCurrentPhotoPath: String? =null

    fun setCurrentPath(path: String?){
        mCurrentPhotoPath = path
    }

    fun selectImage(context: Context, packageName: String)
    {
        val c= context as Activity

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Add Photo!")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val f = File(Environment.getExternalStorageDirectory(), "temp.jpg")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f))
                takePicture(context, packageName)
            } else if (options[item] == "Choose from Gallery") {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                c.startActivityForResult(intent, 2)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    fun takePicture(
        context: Context,
        packageName: String
    ) {
        val c= context as Activity
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile(c)

        uri = FileProvider.getUriForFile(
            context,
            packageName,
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        c.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        //To get the File for further usage
    }

    fun cropImage(context: Context, uri2: Uri = uri!!) {
        val c = context as Activity
        try {
            val cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri2, "image/*")
            Log.d("CropIntent","$uri2 $mCurrentPhotoPath")
            cropIntent.putExtra("crop", "true")
            cropIntent.putExtra("aspectX", 10)
            cropIntent.putExtra("aspectY", 10)
            cropIntent.putExtra("outputX", 512)
            cropIntent.putExtra("outputY", 512)
            cropIntent.putExtra("return-data", true)
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(mCurrentPhotoPath)))
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            c.startActivityForResult(cropIntent, 3)
        } catch (e: ActivityNotFoundException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //To get the File for further usage
            val auxFile = File(mCurrentPhotoPath)
            var bitmap: Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)

        }
    }
    private fun createFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }
}