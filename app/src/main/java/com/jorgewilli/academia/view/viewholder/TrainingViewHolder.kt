package com.jorgewilli.academia.view.viewholder

import android.app.AlertDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jorgewilli.academia.R
import com.jorgewilli.academia.service.listener.TrainingListener
import com.jorgewilli.academia.service.model.TrainingModel
import java.util.*

class TrainingViewHolder(itemView: View, val listener: TrainingListener) :
    RecyclerView.ViewHolder(itemView) {

    private val mDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    private var mTextDescription: TextView = itemView.findViewById(R.id.text_description)
    private var mTextDueDate: TextView = itemView.findViewById(R.id.text_due_date)
    private var mImageEdite: ImageView = itemView.findViewById(R.id.image_training_edit)

    fun bindData(training: TrainingModel) {

        this.mTextDescription.text = training.descricao

        val date = SimpleDateFormat("yyyy-MM-dd").parse(training.data)
        this.mTextDueDate.text = mDateFormat.format(date)

        // Eventos
        this.mImageEdite.setOnClickListener { listener.onEditeClick(training.key) }

        itemView.setOnClickListener { listener.onListClick(training.nome) }

        itemView.setOnLongClickListener {
            AlertDialog.Builder(itemView.context)
            .setTitle(R.string.remocao_de_training)
            .setMessage(R.string.remover_training)
            .setPositiveButton(R.string.sim) { dialog, which ->
                listener.onDeleteClick(training.key)
            }
            .setNeutralButton(R.string.cancelar, null)
            .show()
            true
        }

    }
}