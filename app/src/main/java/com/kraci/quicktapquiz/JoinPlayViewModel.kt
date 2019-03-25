package com.kraci.quicktapquiz

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*

class JoinPlayViewModelFactory(private val application: Application, private val game: Game): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return JoinPlayViewModel(application, game) as T
    }
}

class JoinPlayViewModel(application: Application, game: Game): AndroidViewModel(application) {

    private val connectionManager = JoinConnectionManager.getInstance(application)
    private val quizGame = game
    private val _answerButtonShouldBeEnabled: MutableLiveData<Boolean> = MutableLiveData(false)

    val answerButtonShouldBeEnabled: LiveData<Boolean>
        get() = _answerButtonShouldBeEnabled

    val joinConnectionCallback = object : JoinConnectionManager.JoinConnectionCallback {

        override fun onConnectionSuccessful(host: String) {
            // toto by nemalo mat zmysel, connection uz je spojene
            Toast.makeText(application.applicationContext, "JOIN PLAY - CONNECTION SUCCESSFUL ??? DAFUQ", Toast.LENGTH_LONG).show()
        }

        override fun onMessageReceived(host: String, message: String) {
            if (message == "RESET") {
                _answerButtonShouldBeEnabled.value = true
            }
        }

        override fun onDisconnected(host: String) {
            // TODO: host vypol hru, vratit sa do main menu
            Toast.makeText(application.applicationContext, "JOIN PLAY - DISCONNECTED EVENT", Toast.LENGTH_LONG).show()
        }

    }

    init {
        connectionManager.registerCallback(joinConnectionCallback)
    }

    fun answerButtonClicked() {
        connectionManager.sendMessage(quizGame.hostID, "ANSWER")
        _answerButtonShouldBeEnabled.value = false
    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(joinConnectionCallback)
    }

}