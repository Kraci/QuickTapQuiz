package com.kraci.quicktapquiz

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*

class JoinPlayViewModel(application: Application): AndroidViewModel(application) {

    private val connectionManager = JoinConnectionManager.getInstance(application)
    private val _answerButtonShouldBeEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _disconnectedFromHost: LiveEvent<Any> = LiveEvent()

    val answerButtonShouldBeEnabled: LiveData<Boolean>
        get() = _answerButtonShouldBeEnabled

    val disconnectedFromHost: LiveEvent<Any>
        get() = _disconnectedFromHost

    val joinConnectionCallback = object : JoinConnectionManager.JoinConnectionCallback {

        override fun onConnectionSuccessful(host: String) { }

        override fun onMessageReceived(host: String, message: String) {
            if (message == "RESET") {
                _answerButtonShouldBeEnabled.value = true
            } else if (message == "DISABLE") {
                _answerButtonShouldBeEnabled.value = false
            }
        }

        override fun onDisconnected(host: String) {
            connectionManager.host = null
            _disconnectedFromHost.call()
        }

    }

    init {
        connectionManager.registerCallback(joinConnectionCallback)
    }

    fun answerButtonClicked() {
        connectionManager.sendMessageToHost("ANSWER")
        _answerButtonShouldBeEnabled.value = false
    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(joinConnectionCallback)
    }

    fun disconnectFromHost() {
        connectionManager.disconnectFromHost()
    }

}