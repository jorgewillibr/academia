package com.jorgewilli.academia.service.listener

import android.net.Uri

interface ExerciseListener {

    /**
     * Click para edição
     */
    fun onListClick(id: String, nome: Int)

    /**
     * Remoção
     */
    fun onDeleteClick(id: String)
}