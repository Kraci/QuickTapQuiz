package com.kraci.quicktapquiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: QuizRepository
    val allQuizzes: LiveData<List<Quiz>>

    init {
        val quizzesDao = QuizDatabase.getDatabase(application).quizDao()
        repository = QuizRepository(quizzesDao)
        allQuizzes = repository.allQuizzes
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun insert(quiz: Quiz) = scope.launch(Dispatchers.IO) {
        repository.insert(quiz)
    }

}