package com.jorgewilli.academia.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jorgewilli.academia.R
import com.jorgewilli.academia.service.listener.TrainingListener
import com.jorgewilli.academia.service.model.TrainingModel
import com.jorgewilli.academia.view.viewholder.TrainingViewHolder

class TrainingAdapter: RecyclerView.Adapter<TrainingViewHolder>() {

    private var mList: List<TrainingModel> = arrayListOf()
    private lateinit var mListener: TrainingListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val item =
            LayoutInflater.from(parent.context).inflate(R.layout.row_training_list, parent, false)
        return TrainingViewHolder(item, mListener)
    }

    override fun getItemCount(): Int {
        return mList.count()
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        holder.bindData(mList[position])
    }

    fun attachListener(listener: TrainingListener) {
        mListener = listener
    }

    fun updateList(list: List<TrainingModel>) {
        mList = list
        notifyDataSetChanged()
    }
}