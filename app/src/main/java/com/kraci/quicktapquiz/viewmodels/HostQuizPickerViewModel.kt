package com.kraci.quicktapquiz.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kraci.quicktapquiz.adapters.HostQuizPickerListAdapter
import com.kraci.quicktapquiz.database.Quiz
import com.kraci.quicktapquiz.database.QuizDatabase
import com.kraci.quicktapquiz.database.QuizRepository
import com.kraci.quicktapquiz.utils.LiveEvent
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

class HostQuizPickerViewModel(application: Application) : AndroidViewModel(application), HostQuizPickerListAdapter.ClickListener {

    val adapter = HostQuizPickerListAdapter()
    val allQuizzes: LiveData<List<Quiz>>
    val quizItemClicked: LiveData<Quiz>
        get() = _quizItemClicked

    private val _quizItemClicked: LiveEvent<Quiz> = LiveEvent()
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository: QuizRepository

    init {
        val db = QuizDatabase.getDatabase(application, scope)
        repository = QuizRepository(
            db.quizDao(),
            db.categoryDao(),
            db.questionDao(),
            db.categoryQuestionDao(),
            db.quizGameDao()
        )
        allQuizzes = repository.allQuizzes
        allQuizzes.observeForever {
            adapter.quizzes = it
        }
        adapter.clickListener = this
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    override fun onItemClick(quiz: Quiz) {
        _quizItemClicked.value = quiz
    }

}