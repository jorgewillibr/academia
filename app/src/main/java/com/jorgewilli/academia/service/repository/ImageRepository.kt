package com.jorgewilli.academia.service.repository

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.storage.FirebaseStorage
import com.jorgewilli.academia.R
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.repository.remote.FirebaseClient
import java.io.ByteArrayOutputStream


class ImageRepository(context: Context) : BaseRepository(context) {

    private val mRemoteStorage = FirebaseClient.createService(FirebaseStorage::class.java)

    fun downloadImage(imageView: ImageView, name: String, listener: APIListener<Bitmap>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        val reference =
            mRemoteStorage?.reference?.child(AcademiaConstants.FIREBASE_STORANGE.TRAINING)
                ?.child("${name}.jpg")
        reference?.downloadUrl?.addOnSuccessListener { task ->
            val url = task
            Log.d(mContext.getString(R.string.TAG_FIREBASE_URL), url.toString())


            Glide.with(mContext).asBitmap().load(url).listener(object : RequestListener<Bitmap> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    Toast.makeText(mContext,
                        mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + e.toString(),
                        Toast.LENGTH_SHORT).show()
                    listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + e.toString())
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    Toast.makeText(mContext,
                        mContext.getString(R.string.SUCCESS_GLIDE_DOWNLOAD),
                        Toast.LENGTH_SHORT).show()
                    listener.onSuccess(resource!!, 200)
                    return true
                }

            }).into(imageView)
        }?.addOnFailureListener { erro ->
            Toast.makeText(mContext,
                mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + erro.message.toString(),
                Toast.LENGTH_SHORT).show()
            listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + erro.message.toString())
        }
    }

    fun getImageGallery(
        activity: Activity?,
    ) {

        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        checkPermissions(activity, 100, permissions)

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

        activity?.startActivityForResult(Intent.createChooser(intent, "Escolha uma Imagem"), 11)
    }

    fun getImageCam(launch: ActivityResultLauncher<Void?>, activity: Activity?){
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        checkPermissions(activity, 100, permissions)

        launch.launch(null)
    }


    //redimecionar imagem, imagem selecionada anterior (bom)
    fun UploadImage(uri_Imagem: Uri, name: String, listener: APIListener<Uri>){

        Glide.with(mContext).asBitmap().load(uri_Imagem)
            .apply(RequestOptions.overrideOf(1024,768))
            .listener(object : RequestListener<Bitmap> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean,
                ): Boolean {

                    Toast.makeText(mContext,
                        mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + e.toString(),
                        Toast.LENGTH_SHORT).show()
                    listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + e.toString())

                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {

                    val baos = ByteArrayOutputStream()
                    resource?.compress(Bitmap.CompressFormat.JPEG, 50, baos)

                    val reference =
                        mRemoteStorage.reference.child(AcademiaConstants.FIREBASE_STORANGE.TRAINING).child("${name}.jpg")
                    val data = baos.toByteArray()
                    val uploadTask = reference.putBytes(data)
                    var uriFirebase: Uri

                    uploadTask.continueWithTask { task ->

                        if (!task.isSuccessful) {
                            task.exception.let {
                                throw it!!
                            }
                        }
                        reference.downloadUrl
                    }

                    .addOnFailureListener { erro ->

                        listener.onFailure(erro.message.toString())

                    }.addOnSuccessListener { taskSnapshot ->
                            uriFirebase = taskSnapshot
                        listener.onSuccess(uriFirebase,200)
                    }

                    Toast.makeText(mContext,
                        mContext.getString(R.string.SUCCESS_GLIDE_DOWNLOAD),
                        Toast.LENGTH_SHORT).show()
                    //listener.onSuccess(uriFirebase, 200)

                    return true
                }

            }).submit()
    }

    fun buscaUrl( name: String, listener: APIListener<Uri>){

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        val reference =
            mRemoteStorage.reference.child(AcademiaConstants.FIREBASE_STORANGE.TRAINING).child("${name}.jpg")
        reference.downloadUrl.addOnSuccessListener { task ->
            val url = task
            listener.onSuccess(url,200)
            Log.d(mContext.getString(R.string.TAG_FIREBASE_URL), url.toString())

        }.addOnFailureListener { erro ->
            Toast.makeText(mContext,
                mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + erro.message.toString(),
                Toast.LENGTH_SHORT).show()
            listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + erro.message.toString())
        }

    }

}