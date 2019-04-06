package com.kraci.quicktapquiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

class ManageQuizzesViewModel(application: Application): AndroidViewModel(application), ManageQuizzesListAdapter.ClickListener {

    val adapter = ManageQuizzesListAdapter()
    val allQuizzes: LiveData<List<Quiz>>

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository: QuizRepository

    private val _addQuizEvent: LiveEvent<Any> = LiveEvent()

    val addQuizEvent: LiveEvent<Any>
        get() = _addQuizEvent

    init {
        val db = QuizDatabase.getDatabase(application, scope)
        repository = QuizRepository(db.quizDao(), db.categoryDao(), db.questionDao(), db.categoryQuestionDao(), db.quizGameDao())
        allQuizzes = repository.allQuizzes
        allQuizzes.observeForever {
            adapter.quizzes = it
        }
        adapter.clickListener = this
    }

    fun addQuizClicked() {
        _addQuizEvent.call()
    }

    override fun onDeleteItemClick(quiz: Quiz) {
        delete(quiz)
    }

    fun delete(quiz: Quiz) = scope.launch(Dispatchers.IO) {
        repository.delete(quiz)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}