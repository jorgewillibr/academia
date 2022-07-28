package com.jorgewilli.academia.service.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jorgewilli.academia.service.model.TrainingModel

@Dao
interface TrainingDAO {

    @Insert
    fun save(list: List<TrainingModel>)

    @Query("DELETE FROM Treino")
    fun clear()

    @Query("SELECT * FROM Treino")
    fun list(): List<TrainingModel>

    @Query("SELECT descricao FROM Treino WHERE id = :id")
    fun getDescription(id: String): String

    @Query("SELECT data FROM Treino WHERE id = :id")
    fun getDate(id: String): String

}