package com.kraci.quicktapquiz

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import androidx.lifecycle.MutableLiveData

class MainMenuViewModel : ViewModel(), LifecycleObserver {

    private val activityEvent: MutableLiveData<Class<out AppCompatActivity>> = MutableLiveData()

    fun activityEvent(): LiveData<Class<out AppCompatActivity>> {
        return activityEvent
    }

    fun activityEventAction(view: View) {
        val button = (view as Button).text
        activityEvent.value = if (button.equals("Host Game")) HostQuizPickerActivity::class.java else HostQuizPickerActivity::class.java
    }

}