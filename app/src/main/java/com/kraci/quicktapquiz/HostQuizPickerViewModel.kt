package com.kraci.quicktapquiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

class HostQuizPickerViewModel(application: Application) : AndroidViewModel(application), HostQuizPickerListAdapter.ClickListener, LifecycleObserver {

    val adapter = HostQuizPickerListAdapter()
    private val quizItemObservable: MutableLiveData<Quiz> = MutableLiveData()

    fun quizItemObservable(): LiveData<Quiz> {
        return quizItemObservable
    }

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: QuizRepository
    val allQuizzes: LiveData<List<Quiz>>

    init {
        val quizzesDao = QuizDatabase.getDatabase(application, scope).quizDao()
        repository = QuizRepository(quizzesDao)
        allQuizzes = repository.allQuizzes
        adapter.clickListener = this
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    override fun onItemClick(quiz: Quiz) {
        quizItemObservable.value = quiz
    }

    fun insert(quiz: Quiz) = scope.launch(Dispatchers.IO) {
        repository.insert(quiz)
    }

    fun deleteAll() = scope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

}