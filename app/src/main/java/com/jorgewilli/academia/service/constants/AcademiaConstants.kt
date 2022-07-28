package com.jorgewilli.academia.service.constants

class AcademiaConstants  private constructor() {
    // SharedPreferences
    object SHARED {
        const val TOKEN_KEY = "tokenkey"
        const val PERSON_KEY = "personkey"
        const val PERSON_NAME = "personname"
    }

    object BUNDLE {
        const val TRAININGID = "trainingkid"
        const val EXERCISEID = "exerciseid"
    }

    object FIREBASE_STORANGE {
        const val TRAINING = "training"
        const val EXERCISE = "exercise"
    }
}