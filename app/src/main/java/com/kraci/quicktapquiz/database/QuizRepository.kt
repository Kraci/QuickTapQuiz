package com.kraci.quicktapquiz.database

import androidx.lifecycle.LiveData
import androidx.annotation.WorkerThread

class QuizRepository(private val quizDao: QuizDao,
                     private val categoryDao: CategoryDao,
                     private val questionDao: QuestionDao,
                     private val categoryQuestionDao: CategoryQuestionDao,
                     private val quizGameDao: QuizGameDao
) {

    val allQuizzes: LiveData<List<Quiz>> = quizDao.allQuizzes()

    fun quizGame(quizId: Int): LiveData<List<QuizGameDao.QuizGameDB>> {
        return quizGameDao.gameForQuiz(quizId)
    }

    // Quiz

    @WorkerThread
    suspend fun insert(quiz: Quiz): Long {
        return quizDao.insert(quiz)
    }

    @WorkerThread
    suspend fun delete(quiz: Quiz) {
        quizDao.delete(quiz)
    }

    @WorkerThread
    suspend fun deleteAllQuizzes() {
        quizDao.deleteAll()
    }

    // Category

    @WorkerThread
    suspend fun insert(category: Category): Long {
        return categoryDao.insert(category)
    }

    // Question

    @WorkerThread
    suspend fun insert(question: Question): Long {
        return questionDao.insert(question)
    }

    // Category Question

    @WorkerThread
    suspend fun insert(categoryQuestion: CategoryQuestion) {
        categoryQuestionDao.insert(categoryQuestion)
    }

}