package com.jorgewilli.academia.service.listener

interface TrainingListener {

    /**
     * Click para seleção
     */
    fun onListClick(id: Int)

    /**
     * Click para edição
     */
    fun onEditeClick(id: String)

    /**
     * Remoção
     */
    fun onDeleteClick(id: String)
}