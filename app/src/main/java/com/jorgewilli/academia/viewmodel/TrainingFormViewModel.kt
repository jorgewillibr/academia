package com.jorgewilli.academia.viewmodel

import android.app.Application
import android.widget.Button
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.listener.ValidationListener
import com.jorgewilli.academia.service.model.TrainingModel
import com.jorgewilli.academia.service.repository.TrainingRepository

class TrainingFormViewModel(application: Application) : AndroidViewModel(application) {

    private val mTrainingRepository = TrainingRepository(application)

    private val mTraining = MutableLiveData<TrainingModel>()
    val training: LiveData<TrainingModel> = mTraining

    private val mDateButton = MutableLiveData<String>()
    val dateButton: LiveData<String> = mDateButton

    private val mValidation = MutableLiveData<ValidationListener>()
    val validation: LiveData<ValidationListener> = mValidation

    fun setDate(data: String){
        mDateButton.value = data
    }

    /**
     * Carregamente de uma tarefas
     */
    fun load(trainingkey: String) {
        mTrainingRepository.load(trainingkey, AcademiaConstants.FIREBASE_STORANGE.TRAINING, object : APIListener<TrainingModel> {
            override fun onSuccess(result: TrainingModel, statusCode: Int) {
                mTraining.value = result
            }

            override fun onFailure(message: String) {
                mTraining.value = null
                mValidation.value = ValidationListener(message)
            }

        })
    }

    fun save(training: TrainingModel) {
        if (training.nome == 0) {
            mTrainingRepository.create(training, AcademiaConstants.FIREBASE_STORANGE.TRAINING, object : APIListener<Boolean> {
                override fun onSuccess(result: Boolean, statusCode: Int) {
                    mValidation.value = ValidationListener()
                }

                override fun onFailure(message: String) {
                    mValidation.value = ValidationListener(message)
                }

            })
        } else {
            mTrainingRepository.update(training, AcademiaConstants.FIREBASE_STORANGE.TRAINING, object : APIListener<Boolean> {
                override fun onSuccess(result: Boolean, statusCode: Int) {
                    mValidation.value = ValidationListener()
                }

                override fun onFailure(message: String) {
                    mValidation.value = ValidationListener(message)
                }

            })
        }
    }

}