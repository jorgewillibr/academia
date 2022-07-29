package com.jorgewilli.academia.service.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.jorgewilli.academia.R
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.model.TrainingModel
import com.jorgewilli.academia.service.repository.local.AcademyDatabase
import com.jorgewilli.academia.service.repository.remote.FirebaseClient

class FireStoreRepository(context: Context) : BaseRepository(context) {

    private val mRemoteDatabase = FirebaseClient.createService(FirebaseFirestore::class.java)
    private val mDatabaseTraining = AcademyDatabase.getDatabase(context).trainingDAO()
    private val mDatabaseExercise = AcademyDatabase.getDatabase(context).exerciseDAO()

    fun getCategory(category: String, listener: APIListener<TrainingModel>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        mRemoteDatabase.collection("Exercicio").document("exercicio01").get().
        addOnFailureListener { erro ->
            Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Erro ${erro.message.toString()}")
            Toast.makeText(mContext,
                mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + "Erro ${erro.message.toString()}",Toast.LENGTH_SHORT).show()
            listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro ${erro.message.toString()}")

        }.addOnSuccessListener { documento ->

            if (documento != null && documento.exists()) {
                val treino = documento.toObject(TrainingModel::class.java)
                Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Sucesso")
                listener.onSuccess(treino!!, 200)
            }else{
                Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Erro Documento")
                Toast.makeText(mContext,
                    mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + "Erro Documento",Toast.LENGTH_SHORT).show()
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro Documento")
            }
        }
    }


    //"Exercicio"
    inline fun <reified T>getListCategory(category: String, listener: APIListener<List<T>>, mRemoteDatabase: FirebaseFirestore) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        mRemoteDatabase.collection(category).get().addOnFailureListener { erro ->
            Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Erro ${erro.message.toString()}")
            Toast.makeText(mContext,
                mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + "Erro ${erro.message.toString()}",Toast.LENGTH_SHORT).show()
            listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro ${erro.message.toString()}")
        }.addOnSuccessListener { documentos ->
            val listObjetos = mutableListOf<T>()

            if (documentos != null) {

                for (documento in documentos) {
                    val key = documento.id
                    val retorno = documento.toObject( T::class.java)
                    listObjetos.add(retorno)
                }
                Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Sucesso 01")
                listener.onSuccess(listObjetos, 200)
            }else{
                Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Erro Documento")
                Toast.makeText(mContext,
                    mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + "Erro Documento",Toast.LENGTH_SHORT).show()
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro Documento")
            }
        }
    }

    fun getListCategoryListner(category: String, listener: APIListener<List<TrainingModel>>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        mRemoteDatabase.collection(category).addSnapshotListener{ documentos, erro ->
            val listTreinos = mutableListOf<TrainingModel>()

            if (erro != null){
                Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Erro Documento")
                Toast.makeText(mContext,
                    mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + "Erro Documento",Toast.LENGTH_SHORT).show()
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro Documento")
            }else if (documentos != null ){
                listTreinos.clear()

                for (documento in documentos) {
                    val key = documento.id
                    val treino = documento.toObject(TrainingModel::class.java)
                    listTreinos.add(treino)
                }
                Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Sucesso")
                listener.onSuccess(listTreinos, 200)
            }else{
                Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Erro Documento")
                Toast.makeText(mContext,
                    mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + "Erro Documento",Toast.LENGTH_SHORT).show()
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro Documento")

            }
        }
    }

}