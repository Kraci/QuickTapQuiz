package com.kraci.quicktapquiz

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HostTeamsWaitingViewModel: ViewModel(), LifecycleObserver {

    private val _teamsJoined: MutableLiveData<List<Int>> = MutableLiveData()
    private val _startQuizButton: LiveEvent<Any> = LiveEvent()

    init {
        startAdvertise()
        _teamsJoined.value = listOf(1 , 2, 3)
    }

    val startQuizButton: LiveData<Any>
        get() = _startQuizButton

    val teamsJoined: LiveData<List<Int>>
        get() = _teamsJoined

    fun startQuizTapped() {
        _startQuizButton.call()
    }

    fun startAdvertise() {
        println("... starting advertise ...")
    }

    val defaultTextViewValue = "Hello, World!"

}