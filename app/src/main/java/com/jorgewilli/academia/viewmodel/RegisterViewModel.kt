package com.jorgewilli.academia.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.listener.ValidationListener
import com.jorgewilli.academia.service.repository.PersonRepository
import com.jorgewilli.academia.service.repository.local.SecurityPreferences

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    // Criação usando API
    private val mCreate = MutableLiveData<ValidationListener>()
    val create: LiveData<ValidationListener> = mCreate

    // Acesso a dados
    private val mSecurityPreferences = SecurityPreferences(application)
    private val mRepository = PersonRepository(application)

    @RequiresApi(Build.VERSION_CODES.P)
    fun create(name: String, email: String, password: String) {
        mRepository.create(name, email, password, object : APIListener<FirebaseUser?> {
            override fun onSuccess(result: FirebaseUser?, statusCode: Int) {

                // Salvar dados do usuário no SharePreferences
                result?.email?.let {
                    mSecurityPreferences.store(AcademiaConstants.SHARED.PERSON_KEY,
                        it)
                }
                result?.let { mSecurityPreferences.store(AcademiaConstants.SHARED.TOKEN_KEY, it.uid) }
                result?.displayName?.let {
                    mSecurityPreferences.store(AcademiaConstants.SHARED.PERSON_NAME,
                        it)
                }

                mCreate.value = ValidationListener()
            }

            override fun onFailure(message: String) {
                mCreate.value = ValidationListener(message)
            }

        })
    }
}