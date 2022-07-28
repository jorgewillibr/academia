package com.jorgewilli.academia.service.repository.remote

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class FirebaseClient {
    companion object {

        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var firebaseFirestore: FirebaseFirestore
        private lateinit var firebaseStorage: FirebaseStorage


        private fun getFirebaseInstanceAuth(): FirebaseAuth {
            if (!Companion::firebaseAuth.isInitialized) {
                firebaseAuth = Firebase.auth
            }
            return firebaseAuth
        }


        private fun getFirebaseInstanceDatabase(): FirebaseFirestore {
            if (!Companion::firebaseFirestore.isInitialized) {
                firebaseFirestore = Firebase.firestore
            }
            return firebaseFirestore
        }


        private fun getFirebaseInstanceStorage(): FirebaseStorage {
            if (!Companion::firebaseStorage.isInitialized) {
                firebaseStorage = Firebase.storage
            }
            return firebaseStorage
        }


        fun <S>createService(serviceClass: Class<S>): S {

            return when(serviceClass){
                FirebaseAuth::class.java -> getFirebaseInstanceAuth() as S
                FirebaseFirestore::class.java -> getFirebaseInstanceDatabase() as S
                FirebaseStorage::class.java -> getFirebaseInstanceStorage() as S
                else -> {
                    FirebaseException("Serviço inexistente!") as S
                }
            }
        }
    }
}