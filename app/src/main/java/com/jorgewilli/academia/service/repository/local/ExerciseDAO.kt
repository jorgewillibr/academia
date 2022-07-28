package com.jorgewilli.academia.service.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jorgewilli.academia.service.model.ExerciseModel

@Dao
interface ExerciseDAO {

    @Insert
    fun save(list: List<ExerciseModel>)

    @Query("DELETE FROM Exercicio")
    fun clear()

    @Query("SELECT * FROM Exercicio")
    fun list(): List<ExerciseModel>

    @Query("SELECT observacoes FROM Exercicio WHERE id = :id")
    fun getObservacoes(id: String): String

    @Query("SELECT imagem FROM Exercicio WHERE id = :id")
    fun getUrlImage(id: String): String

}