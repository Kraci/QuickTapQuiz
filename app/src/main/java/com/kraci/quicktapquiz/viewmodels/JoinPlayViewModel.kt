package com.kraci.quicktapquiz.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.kraci.quicktapquiz.connections.HostConnectionManager
import com.kraci.quicktapquiz.connections.JoinConnectionManager
import com.kraci.quicktapquiz.utils.LiveEvent

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
            if (message == HostConnectionManager.RESET) {
                _answerButtonShouldBeEnabled.value = true
            } else if (message == HostConnectionManager.DISABLE) {
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
        connectionManager.sendMessageToHost(JoinConnectionManager.ANSWER)
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