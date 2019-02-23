package com.kraci.quicktapquiz

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository
    val allQuizzes: LiveData<List<Quiz>>

    init {
        val quizzesDao = QuizDatabase.getDatabase(application).quizDao()
        repository = QuizRepository(quizzesDao)
        allQuizzes = repository.allQuizzes
    }

}