package com.jorgewilli.academia.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.listener.ValidationListener
import com.jorgewilli.academia.service.model.ExerciseModel
import com.jorgewilli.academia.service.repository.ExerciseRepository
import com.jorgewilli.academia.service.repository.FireStoreRepository
import com.jorgewilli.academia.service.repository.ImageRepository
import java.io.ByteArrayOutputStream


class ExerciseFormViewModel( application: Application) : AndroidViewModel(application) {

    private val mExerciseRepository = ExerciseRepository(application)
    private val mFireStoreRepository = FireStoreRepository(application)
    private val mImageRepository = ImageRepository(application)

    private val mExercise = MutableLiveData<ExerciseModel>()
    val exercise: LiveData<ExerciseModel> = mExercise

    private val mProgressBar = MutableLiveData<Boolean>()
    val progressBar: LiveData<Boolean> = mProgressBar

    private val mImage = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap> = mImage

    private val mUriImagem = MutableLiveData<Uri>()
    val uriImagem: LiveData<Uri> = mUriImagem

    private val mValidation = MutableLiveData<ValidationListener>()
    val validation: LiveData<ValidationListener> = mValidation

    /**
     * Carregamente de uma tarefas
     */
    fun load(exerciseId: String) {
        mExerciseRepository.load(exerciseId, AcademiaConstants.FIREBASE_STORANGE.EXERCISE, object : APIListener<ExerciseModel> {
            override fun onSuccess(result: ExerciseModel, statusCode: Int) {
                mExercise.value = result
            }

            override fun onFailure(message: String) {
                mExercise.value = null
                mValidation.value = ValidationListener(message)
            }

        })
    }

    fun save(exercise: ExerciseModel) {
        if (exercise.key == "") {
            mExerciseRepository.create(exercise, AcademiaConstants.FIREBASE_STORANGE.EXERCISE, object : APIListener<Boolean> {
                override fun onSuccess(result: Boolean, statusCode: Int) {
                    mValidation.value = ValidationListener()
                }

                override fun onFailure(message: String) {
                    mValidation.value = ValidationListener(message)
                }

            })
        } else {
            mExerciseRepository.update(exercise, AcademiaConstants.FIREBASE_STORANGE.EXERCISE, object : APIListener<Boolean> {
                override fun onSuccess(result: Boolean, statusCode: Int) {
                    mValidation.value = ValidationListener()
                }

                override fun onFailure(message: String) {
                    mValidation.value = ValidationListener(message)
                }

            })
        }
    }

    fun deleteImage(){

    }

    fun downloadImage(imageView: ImageView){
        mProgressBar.value = true
        mImageRepository.downloadImage(imageView,"Eu", object : APIListener<Bitmap>{
            override fun onSuccess(result: Bitmap, statusCode: Int) {
                mImage.value = result
                mProgressBar.value = false
            }

            override fun onFailure(message: String) {

                mProgressBar.value = false
            }

        })
    }

    fun uploadImage(){
        mProgressBar.value = true
        mUriImagem.value?.let {
            mImageRepository.UploadImage(it,"Eu",  object : APIListener<Uri>{
                override fun onSuccess(result: Uri, statusCode: Int) {
                    //mImage.value = result
                    mProgressBar.value = false
                }

                override fun onFailure(message: String) {

                    mProgressBar.value = false
                }

            })
        }
    }

    fun getImageGallery(imageView: ImageView, activity: Activity?){
        mImageRepository.getImageGallery( activity)
    }

    fun getImageCam(launch: ActivityResultLauncher<Void?> ,activity: Activity?){
        mImageRepository.getImageCam(launch,activity)
    }

    fun setUri(uri: Uri){
        mUriImagem.value = uri
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
            inImage,
            "Title",
            null)
        return Uri.parse(path)
    }

}