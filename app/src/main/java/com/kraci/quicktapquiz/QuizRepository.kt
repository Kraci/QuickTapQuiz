package com.kraci.quicktapquiz

import androidx.lifecycle.LiveData
import androidx.annotation.WorkerThread

class QuizRepository(private val quizDao: QuizDao,
                     private val categoryDao: CategoryDao,
                     private val questionDao: QuestionDao,
                     private val categoryQuestionDao: CategoryQuestionDao,
                     private val quizGameDao: QuizGameDao) {

    val allQuizzes: LiveData<List<Quiz>> = quizDao.allQuizzes()
    fun quizGame(quizId: Int): LiveData<List<QuizGameDao.QuizGameDB>> {
        return quizGameDao.gameForQuiz(quizId)
    }

    @WorkerThread
    suspend fun insert(quiz: Quiz) {
        quizDao.insert(quiz)
    }

    @WorkerThread
    suspend fun deleteAll() {
        quizDao.deleteAll()
    }

}