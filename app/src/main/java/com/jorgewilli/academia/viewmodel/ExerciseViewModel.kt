package com.jorgewilli.academia.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.listener.ValidationListener
import com.jorgewilli.academia.service.model.ExerciseModel
import com.jorgewilli.academia.service.repository.ExerciseRepository
import com.jorgewilli.academia.service.repository.FireStoreRepository
import com.jorgewilli.academia.service.repository.remote.FirebaseClient

class ExerciseViewModel(application: Application) : AndroidViewModel(application){

    private val mValidation = MutableLiveData<ValidationListener>()
    val validation: LiveData<ValidationListener> = mValidation

    private val mExerciseList = MutableLiveData<List<ExerciseModel>>()
    val exerciseList: LiveData<List<ExerciseModel>> = mExerciseList

    private val mImage = MutableLiveData<ImageView>()
    val image: LiveData<ImageView> = mImage

    private val mTrainingId = MutableLiveData<Int>()
    val trainingId: LiveData<Int> = mTrainingId

    // Acesso a dados
    private val mExerciseRepository: ExerciseRepository = ExerciseRepository(application)
    private val mFireStoreRepository = FireStoreRepository(application)
    private val mRemoteDatabase = FirebaseClient.createService(FirebaseFirestore::class.java)

    /**
     * Carregamente de uma tarefas
     */
    fun load(trainingId: Int) {
        mTrainingId.value = trainingId
    }

    fun list() {
        val listener = object : APIListener<List<ExerciseModel>> {
            override fun onSuccess(result: List<ExerciseModel>, statusCode: Int) {
                mExerciseList.value = result
            }

            override fun onFailure(message: String) {
                mExerciseList.value = null
                mValidation.value = ValidationListener(message)
            }
        }
        mExerciseRepository.listExercises(mTrainingId.value,AcademiaConstants.FIREBASE_STORANGE.EXERCISE,listener)
    }

    fun deleteTask(id: String) {
        mExerciseRepository.delete(id,AcademiaConstants.FIREBASE_STORANGE.EXERCISE, object : APIListener<Boolean> {
            override fun onSuccess(result: Boolean, statusCode: Int) {
                mValidation.value = ValidationListener()
                list()
            }

            override fun onFailure(message: String) {
                mValidation.value = ValidationListener(message)
            }

        })
    }
}