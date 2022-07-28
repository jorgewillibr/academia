package com.jorgewilli.academia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.listener.ValidationListener
import com.jorgewilli.academia.service.model.TrainingModel
import com.jorgewilli.academia.service.repository.TrainingRepository

class HomeViewModel(application: Application) : AndroidViewModel(application)  {

    private val mValidation = MutableLiveData<ValidationListener>()
    val validation: LiveData<ValidationListener> = mValidation

    private val mTrainingList = MutableLiveData<List<TrainingModel>>()
    val trainingList: LiveData<List<TrainingModel>> = mTrainingList

    // Acesso a dados
    private val mTrainingRepository: TrainingRepository = TrainingRepository(application)

    fun list() {
        val listener = object : APIListener<List<TrainingModel>> {
            override fun onSuccess(result: List<TrainingModel>, statusCode: Int) {
                mTrainingList.value = result
            }

            override fun onFailure(message: String) {
                mTrainingList.value = null
                mValidation.value = ValidationListener(message)
            }
        }
        mTrainingRepository.listTrainings(AcademiaConstants.FIREBASE_STORANGE.TRAINING,listener)
    }

    fun deleteTask(id: String) {
        mTrainingRepository.delete(id, AcademiaConstants.FIREBASE_STORANGE.TRAINING, object : APIListener<Boolean> {
            override fun onSuccess(result: Boolean, statusCode: Int) {
                mValidation.value = ValidationListener()
                list()
            }

            override fun onFailure(message: String) {
                mValidation.value = ValidationListener(message)
            }

        })
    }

//    private fun updateStatus(id: Int, complete: Boolean) {
//        mTrainingRepository.updateStatus(id, complete, object : APIListener<Boolean> {
//            override fun onSuccess(result: Boolean, statusCode: Int) {
//                list()
//            }
//
//            override fun onFailure(message: String) {
//                mValidation.value = ValidationListener(message)
//            }
//        })
//    }
}