package com.kraci.quicktapquiz

import androidx.lifecycle.LiveData
import androidx.annotation.WorkerThread

class QuizRepository(private val quizDao: QuizDao) {

    val allQuizzes: LiveData<List<Quiz>> = quizDao.allQuizzes()

    @WorkerThread
    suspend fun insert(quiz: Quiz) {
        quizDao.insert(quiz)
    }

}