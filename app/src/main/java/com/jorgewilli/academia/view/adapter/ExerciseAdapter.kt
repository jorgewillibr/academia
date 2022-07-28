package com.jorgewilli.academia.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jorgewilli.academia.R
import com.jorgewilli.academia.service.listener.ExerciseListener
import com.jorgewilli.academia.service.model.ExerciseModel
import com.jorgewilli.academia.view.viewholder.ExerciseViewHolder

class ExerciseAdapter: RecyclerView.Adapter<ExerciseViewHolder>() {

    private var mList: List<ExerciseModel> = arrayListOf()
    private lateinit var mListener: ExerciseListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val item =
            LayoutInflater.from(parent.context).inflate(R.layout.row_exercise_list, parent, false)
        return ExerciseViewHolder(item, mListener)

    }

    override fun getItemCount(): Int {
        return mList.count()
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bindData(mList[position])
    }

    fun attachListener(listener: ExerciseListener) {
        mListener = listener
    }

    fun updateList(list: List<ExerciseModel>) {
        mList = list
        notifyDataSetChanged()
    }
}