package com.jorgewilli.academia.service.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jorgewilli.academia.R
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.model.ExerciseModel
import com.jorgewilli.academia.service.model.TrainingModel
import com.jorgewilli.academia.service.repository.local.AcademyDatabase
import com.jorgewilli.academia.service.repository.remote.FirebaseClient
import java.time.LocalDateTime

class ExerciseRepository(context: Context) : BaseRepository(context) {

    //private val mRemote = RetrofitClient.createService(ExerciseService::class.java)
    private val mDatabaseExercise = AcademyDatabase.getDatabase(context).exerciseDAO()

    private val mRemoteStorage = FirebaseClient.createService(FirebaseStorage::class.java)
    private val mImageRepository = ImageRepository(context)
    private val mRemoteDatabase = FirebaseClient.createService(FirebaseFirestore::class.java)

    fun listExercises(id: Int?,category: String, listener: APIListener<List<ExerciseModel>>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        mRemoteDatabase.collection(category)
            .whereEqualTo("nome",id)
            .get()
            .addOnFailureListener { erro ->
            Log.d(mContext.getString(R.string.TAG_FIREBASE_FIRESTOREL), "Erro ${erro.message.toString()}")
            Toast.makeText(mContext,
                mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) + "Erro ${erro.message.toString()}",Toast.LENGTH_SHORT).show()
            listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro ${erro.message.toString()}")
        }.addOnSuccessListener { documentos ->
            val listObjetos = mutableListOf<ExerciseModel>()

            if (documentos != null) {

                for (documento in documentos) {
                    val key = documento.id
                    val retorno = documento.toObject( ExerciseModel::class.java)
                    retorno.key = key
                    mImageRepository.buscaUrl(retorno.key,object : APIListener<Uri>{
                        override fun onSuccess(result: Uri, statusCode: Int) {
                            retorno.imagem = result.toString()
                        }

                        override fun onFailure(message: String) {}
                    })
                    listObjetos.add(retorno)
                }
                listener.onSuccess(listObjetos, 200)
            }else{
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro Documento")
            }
        }
    }

    fun delete(key: String,category: String, listener: APIListener<Boolean>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        mRemoteDatabase.collection(category).document(key)
            .delete()
            .addOnFailureListener { erro ->
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro ${erro.message.toString()}")

            }.addOnSuccessListener { documentos ->

                listener.onSuccess(true, 200)
            }
    }

    fun load(key: String, category: String,  listener: APIListener<ExerciseModel>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        mRemoteDatabase.collection(category).document(key).get().
        addOnFailureListener { erro ->
            listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro ${erro.message.toString()}")

        }.addOnSuccessListener { documento ->
            if (documento != null && documento.exists()) {
                val objeto = documento.toObject(ExerciseModel::class.java)
                listener.onSuccess(objeto!!, 200)

            }else{
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro Documento")
            }
        }
    }

    fun create(exercise: ExerciseModel,category: String, listener: APIListener<Boolean>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        val newKeyExercise = category + LocalDateTime.now().toString()

        mImageRepository.UploadImage(exercise.imagem.toUri(), newKeyExercise,object :APIListener<Uri>{
            override fun onSuccess(result: Uri, statusCode: Int) {
                exercise.imagem = result.toString()
            }

            override fun onFailure(message: String) {

            }
        })

        val newExercise = hashMapOf(
            "imagem" to exercise.imagem,
            "nome" to exercise.nome,
            "observacoes" to exercise.observacoes
        )

        mRemoteDatabase.collection(category).document(newKeyExercise)
            .set(newExercise)
            .addOnFailureListener { erro ->
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro ${erro.message.toString()}")

            }.addOnSuccessListener { documentos ->

                    listener.onSuccess(true, 200)
            }

    }

    fun update(exercise: ExerciseModel,category: String, listener: APIListener<Boolean>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        mImageRepository.UploadImage(exercise.imagem.toUri(), exercise.key,object :APIListener<Uri>{
            override fun onSuccess(result: Uri, statusCode: Int) {
                exercise.imagem = result.toString()
            }

            override fun onFailure(message: String) {

            }
        })

        mRemoteDatabase.collection(category).document(exercise.key)
            .update(
                mapOf(
                "nome" to exercise.nome,
                "observacoes" to exercise.observacoes,
                "imagem" to exercise.imagem
                ))
            .addOnFailureListener { erro ->
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro ${erro.message.toString()}")

            }.addOnSuccessListener { documentos ->

                listener.onSuccess(true, 200)
            }

    }


    fun list() = mDatabaseExercise.list()

    fun save(list: List<ExerciseModel>) {
        mDatabaseExercise.clear()
        mDatabaseExercise.save(list)
    }

    fun getObservacoes(id: String) = mDatabaseExercise.getObservacoes(id)
    fun getUriImage(id: String) = mDatabaseExercise.getUrlImage(id)

}