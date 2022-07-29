package com.jorgewilli.academia.service.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Exercicio")
class ExerciseModel {

    @ColumnInfo(name = "id")
    @PrimaryKey
    var key: String= ""

    @ColumnInfo(name = "nome")
    var nome: Int = 0

    @ColumnInfo(name = "observacoes")
    lateinit var observacoes: String

    @ColumnInfo(name = "imagem")
    lateinit var imagem: String

}