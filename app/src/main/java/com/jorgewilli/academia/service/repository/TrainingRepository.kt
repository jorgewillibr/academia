package com.jorgewilli.academia.service.repository

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.jorgewilli.academia.R
import com.jorgewilli.academia.service.listener.APIListener
import com.jorgewilli.academia.service.model.ExerciseModel
import com.jorgewilli.academia.service.model.TrainingModel
import com.jorgewilli.academia.service.repository.local.AcademyDatabase
import com.jorgewilli.academia.service.repository.remote.FirebaseClient
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class TrainingRepository (context: Context) : BaseRepository(context) {

    private val mDatabaseTraining = AcademyDatabase.getDatabase(context).trainingDAO()
    private val mRemoteDatabase = FirebaseClient.createService(FirebaseFirestore::class.java)

    fun listTrainings(category: String, listener: APIListener<List<TrainingModel>>) {

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
            val listObjetos = mutableListOf<TrainingModel>()

            if (documentos != null) {

                for (documento in documentos) {
                    val key = documento.id
                    try {
                        val retorno = documento.toObject( TrainingModel::class.java)
                        retorno.key = key
                        listObjetos.add(retorno)

                    }catch (e: Exception){

                        val dados = documento.data
                        val nome = dados.getValue("nome").toString()
                        val descricao = dados.get("descricao").toString()

                        val timestamp = dados.get("data") as com.google.firebase.Timestamp
                        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        val netDate = Date(milliseconds)
                        //val date = sdf.format(netDate).toString()

                        val retorno = TrainingModel().apply {
                            this.key = key
                            this.nome = nome.toInt()
                            this.descricao = descricao
                            this.data = sdf.format(netDate).toString()
                        }

                        listObjetos.add(retorno)

                    }
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
//
//    fun updateStatus(id: Int, complete: Boolean, listener: APIListener<Boolean>) {
//
//        // Verificação de internet
//        if (!isConnectionAvailable(mContext)) {
//            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
//            return
//        }
//
//    }

    fun delete(key: String, category: String, listener: APIListener<Boolean>) {

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

    fun load(key: String, category: String, listener: APIListener<TrainingModel>) {

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
                var objeto: TrainingModel
                val key = documento.id
                try {
                    objeto = documento.toObject( TrainingModel::class.java)!!
                    objeto.key = key

                }catch (e: Exception){

                    val dados = documento.data
                    val nome = dados?.getValue("nome").toString()
                    val descricao = dados?.get("descricao").toString()

                    val timestamp = dados?.get("data") as com.google.firebase.Timestamp
                    val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val netDate = Date(milliseconds)
                    //val date = sdf.format(netDate).toString()

                    objeto = TrainingModel().apply {
                        this.key = key
                        this.nome = nome.toInt()
                        this.descricao = descricao
                        this.data = sdf.format(netDate).toString()
                    }

                }
                listener.onSuccess(objeto, 200)

            }else{
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro Documento")
            }
        }

    }

    fun create(training: TrainingModel, category: String, listener: APIListener<Boolean>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        val reference = mRemoteDatabase.collection(category)

        reference.get().addOnSuccessListener { documents ->
            if (documents != null) {
                documents.size()

                val newTraining = hashMapOf(
                    "nome" to documents.size()+1,
                    "descricao" to training.descricao,
                    "data" to training.data,
                )

                reference
                    .add(newTraining)
                    .addOnFailureListener { erro ->
                        listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro ${erro.message.toString()}")

                    }.addOnSuccessListener { documentos ->

                        listener.onSuccess(true, 200)
                    }


            }
        }
    }

    fun update(training: TrainingModel, category: String, listener: APIListener<Boolean>) {

        // Verificação de internet
        if (!isConnectionAvailable(mContext)) {
            listener.onFailure(mContext.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }

        mRemoteDatabase.collection(category).document(training.key)
            .update(
                mapOf(
                    "nome" to training.nome,
                    "descricao" to training.descricao,
                    "data" to training.data
                ))
            .addOnFailureListener { erro ->
                listener.onFailure(mContext.getString(R.string.ERROR_GLIDE_DOWNLOAD) +"Erro ${erro.message.toString()}")

            }.addOnSuccessListener { documentos ->

                listener.onSuccess(true, 200)
            }

    }

    fun list() = mDatabaseTraining.list()

    fun save(list: List<TrainingModel>) {
        mDatabaseTraining.clear()
        mDatabaseTraining.save(list)
    }

    fun getDescription(id: String) = mDatabaseTraining.getDescription(id)
    fun getDate(id: String) = mDatabaseTraining.getDate(id)

}