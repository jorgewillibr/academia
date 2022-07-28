package com.jorgewilli.academia.service.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Treino")
class TrainingModel() {

    @ColumnInfo(name = "id")
    @PrimaryKey
    var key: String = ""

    @ColumnInfo(name = "nome")
    var nome: Int = 0

    @ColumnInfo(name = "descricao")
    lateinit var descricao: String

    @ColumnInfo(name = "data")
    lateinit var data: String

}