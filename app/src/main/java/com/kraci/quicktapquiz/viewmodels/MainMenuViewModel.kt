package com.kraci.quicktapquiz.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.kraci.quicktapquiz.activities.HostQuizPickerActivity
import com.kraci.quicktapquiz.activities.JoinQuizChooseActivity
import com.kraci.quicktapquiz.activities.ManageQuizzesActivity
import com.kraci.quicktapquiz.utils.LiveEvent

class MainMenuViewModel : ViewModel() {

    private val _clickEvent: LiveEvent<Class<out AppCompatActivity>> = LiveEvent()

    val clickEvent: LiveData<Class<out AppCompatActivity>>
        get() = _clickEvent

    fun clickEventAction(view: View) {
        val button = (view as Button)
        if (button.text.equals("Host Quiz")) {
            _clickEvent.value = HostQuizPickerActivity::class.java
        } else if (button.text.equals("Join Quiz")) {
            _clickEvent.value = JoinQuizChooseActivity::class.java
        } else if (button.text.equals("Manage Quizzes")) {
            _clickEvent.value = ManageQuizzesActivity::class.java
        }
    }

}