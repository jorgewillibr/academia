package com.jorgewilli.academia.view.viewholder

import android.app.AlertDialog
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jorgewilli.academia.R
import com.jorgewilli.academia.service.listener.ExerciseListener
import com.jorgewilli.academia.service.model.ExerciseModel

class ExerciseViewHolder(itemView: View, val listener: ExerciseListener) :
    RecyclerView.ViewHolder(itemView) {

    private var mTextDescription: TextView = itemView.findViewById(R.id.text_observacao)
    private var mImageExercicio: ImageView = itemView.findViewById(R.id.image_exercicio)

    fun bindData(exercise: ExerciseModel) {

        this.mTextDescription.text = exercise.observacoes

        Glide.with(itemView).asBitmap().load(exercise.imagem.toUri()).listener(object : RequestListener<Bitmap> {

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
               mImageExercicio.setImageBitmap(resource)
                return true
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean,
            ): Boolean {
                return false
            }

        }).into(this.mImageExercicio)
        //this.mImageExercicio.setImageURI(exercise.imagem.toUri())


            // Eventos
                itemView.setOnClickListener { listener.onListClick(exercise.key, exercise.nome) }
                itemView.setOnLongClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle(R.string.remocao_de_exercise)
                        .setMessage(R.string.remover_exercise)
                        .setPositiveButton(R.string.sim) { dialog, which ->
                            listener.onDeleteClick(exercise.key)
                        }
                        .setNeutralButton(R.string.cancelar, null)
                        .show()
                    true
                }
    }


}