package com.jorgewilli.academia.service.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jorgewilli.academia.service.model.ExerciseModel
import com.jorgewilli.academia.service.model.TrainingModel

@Database(entities = [TrainingModel::class, ExerciseModel::class], version = 1)
abstract class AcademyDatabase : RoomDatabase() {

    abstract fun trainingDAO(): TrainingDAO
    abstract fun exerciseDAO(): ExerciseDAO

    companion object {
        private lateinit var INSTANCE: AcademyDatabase

        fun getDatabase(context: Context): AcademyDatabase {
            if (!Companion::INSTANCE.isInitialized) {
                synchronized(AcademyDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context, AcademyDatabase::class.java, "academyDB")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }

}
