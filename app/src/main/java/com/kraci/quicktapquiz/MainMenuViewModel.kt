package com.kraci.quicktapquiz

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button

class MainMenuViewModel : ViewModel(), LifecycleObserver {

    private val activityEvent: SingleLiveEvent<Class<out AppCompatActivity>> = SingleLiveEvent()

    fun activityEvent(): LiveData<Class<out AppCompatActivity>> {
        return activityEvent
    }

    fun activityEventAction(view: View) {
        val button = (view as Button).text
        activityEvent.value = if (button.equals("Host Game")) HostQuizPickerActivity::class.java else HostQuizPickerActivity::class.java
    }

}