package com.kraci.quicktapquiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import androidx.lifecycle.MutableLiveData

class MainMenuViewModel : ViewModel() {

    private val _clickEvent: MutableLiveData<Class<out AppCompatActivity>> = MutableLiveData()

    val clickEvent: LiveData<Class<out AppCompatActivity>>
        get() = _clickEvent

    fun clickEventAction(view: View) {
        val button = (view as Button)
        _clickEvent.value = if (button.text.equals("Host Quiz")) HostQuizPickerActivity::class.java else JoinQuizChooseActivity::class.java
    }

}