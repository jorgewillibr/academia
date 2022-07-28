package com.jorgewilli.academia.service.repository

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jorgewilli.academia.R
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.repository.remote.FirebaseClient

class PersonRepository(context: Context) : BaseRepository(context) {

    private val mRemoteAuth = FirebaseClient.createService(FirebaseAuth::class.java)


    @RequiresApi(Build.VERSION_CODES.P)
    fun login(email: String, password: String, listener: APIListener<FirebaseUser?>) {
        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        if (mRemoteAuth.currentUser != null) {
            listener.onSuccess(mRemoteAuth.currentUser, 200)
        }

        mRemoteAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(mContext.mainExecutor) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(mContext.getString(R.string.TAG_FIREBASE_LOGIN), "signInWithEmail:success")
                    val user = mRemoteAuth.currentUser
                    listener.onSuccess(user, 200)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(mContext.getString(R.string.TAG_FIREBASE_LOGIN), "signInWithEmail:failure", task.exception)
                    Toast.makeText(mContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    listener.onFailure(task.exception?.message.toString())
                }
            }
    }

    fun logOut(listener: APIListener<FirebaseUser?>){
        try {
            mRemoteAuth.signOut()
            Log.d(mContext.getString(R.string.TAG_FIREBASE_LOOUT), "signOut:success")
            listener.onSuccess(null, 200)
        }catch (e: FirebaseException){
            listener.onFailure(e.message.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun create(
        name: String,
        email: String,
        password: String,
        listener: APIListener<FirebaseUser?>,
    ) {
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        if (mRemoteAuth.currentUser != null) {
            listener.onSuccess(mRemoteAuth.currentUser, 200)
        }

        mRemoteAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(mContext.mainExecutor) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(mContext.getString(R.string.TAG_FIREBASE_CRIATE_LOGIN), "createUserWithEmail:success")
                    val user = mRemoteAuth.currentUser
                    listener.onSuccess(user, 200)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(mContext.getString(R.string.TAG_FIREBASE_CRIATE_LOGIN), "createUserWithEmail:failure", task.exception)
                    Toast.makeText(mContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    listener.onFailure(task.exception?.message.toString())
                }
            }
    }
}