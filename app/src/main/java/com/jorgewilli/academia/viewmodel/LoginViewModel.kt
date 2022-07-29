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


class LoginViewModel(application: Application) : AndroidViewModel(application)  {
    // Acesso a dados
    private val mSecurityPreferences = SecurityPreferences(application)
    private val mPersonRepository = PersonRepository(application)
//    private val mPriorityRepository = PriorityRepository(application)

    // Login usando API
    private val mLogin = MutableLiveData<ValidationListener>()
    val login: LiveData<ValidationListener> = mLogin

    // Login usando SharedPreferences
    private val mLoggedUser = MutableLiveData<Boolean>()
    val loggedUser: LiveData<Boolean> = mLoggedUser

    /**
     * Faz login usando API
     */

    @RequiresApi(Build.VERSION_CODES.P)
    fun doLogin(email: String, password: String) {
        mPersonRepository.login(email, password, object : APIListener<FirebaseUser?> {
            override fun onSuccess(result: FirebaseUser?, statusCode: Int) {

                // Salvar dados do usuário no SharePreferences
                result?.email?.let {mSecurityPreferences.store(AcademiaConstants.SHARED.PERSON_KEY,it)}
                result?.let { mSecurityPreferences.store(AcademiaConstants.SHARED.TOKEN_KEY, it.uid) }
                result?.displayName?.let {mSecurityPreferences.store(AcademiaConstants.SHARED.PERSON_NAME,it)}

                // Informa sucesso
                mLogin.value = ValidationListener()
            }

            override fun onFailure(message: String) {
                mLogin.value = ValidationListener(message)
            }
        })
    }

    /**
     * Verifica se usuário está logado
     */
    fun verifyLoggedUser() {
        val personKey = mSecurityPreferences.get(AcademiaConstants.SHARED.PERSON_KEY)
        val tokenKey = mSecurityPreferences.get(AcademiaConstants.SHARED.TOKEN_KEY)

        // Se token e person key forem diferentes de vazio, usuário está logado
        val logged = (tokenKey != "" && personKey != "")

        // Atualiza o valor
        mLoggedUser.value = logged

        // Se usuário não estiver logado, aplicação vai atualizar os dados
        if (!logged) {
//            mPriorityRepository.all(object : APIListener<List<PriorityModel>> {
//                override fun onSuccess(result: List<PriorityModel>, statusCode: Int) {
//                    mPriorityRepository.save(result)
//                }
//
//                // Erro silencioso
//                override fun onFailure(message: String) {
//                    val s = ""
//                }
//
//            })
        }
    }
}