package com.jorgewilli.academia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.jorgewilli.academia.service.constants.AcademiaConstants
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.listener.ValidationListener
import com.jorgewilli.academia.service.repository.PersonRepository
import com.jorgewilli.academia.service.repository.local.SecurityPreferences

class NavHeaderViewModel(application: Application) : AndroidViewModel(application) {
    // Acesso a dados
    private val mSecurityPreferences = SecurityPreferences(application)
    private val mPersonRepository = PersonRepository(application)

    private val mNome = MutableLiveData<String>().apply {
        value = "Olá"


    }
    val nome: LiveData<String> = mNome

    private val mEmail = MutableLiveData<String>().apply {
        value = mSecurityPreferences.get(AcademiaConstants.SHARED.PERSON_KEY)
    }
    val email: LiveData<String> = mEmail

//    private val mFoto = MutableLiveData<String>().apply {
//        value = ""
//    }
//    val foto: LiveData<String> = mFoto

    // Login usando API
    private val mLogin = MutableLiveData<ValidationListener>()
    val login: LiveData<ValidationListener> = mLogin

    fun doSignOut(){
        mPersonRepository.logOut(object : APIListener<FirebaseUser?>{
            override fun onSuccess(result: FirebaseUser?, statusCode: Int) {

                mSecurityPreferences.remove(AcademiaConstants.SHARED.PERSON_KEY)

                // Informa sucesso
                mLogin.value = ValidationListener()
            }

            override fun onFailure(message: String) {
                mLogin.value = ValidationListener(message)
            }

        })
    }
}