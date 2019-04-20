package com.kraci.quicktapquiz.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.kraci.quicktapquiz.adapters.HostTeamsWaitingListAdapter
import com.kraci.quicktapquiz.connections.HostConnectionManager
import com.kraci.quicktapquiz.connections.JoinConnectionManager
import com.kraci.quicktapquiz.utils.LiveEvent

class JoinTeamsWaitingViewModelFactory(private val application: Application, private val hostGame: HostedGame) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return JoinTeamsWaitingViewModel(application, hostGame) as T
    }
}

class JoinTeamsWaitingViewModel(application: Application, private val hostGame: HostedGame) : AndroidViewModel(application) {

    private val _teamsJoined: MutableLiveData<List<Team>> = MutableLiveData()
    private val _readyButtonShouldBeActive: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _startQuizEvent: LiveEvent<HostedGame> = LiveEvent()
    private val _diconnectedFromHost: LiveEvent<Any> =
        LiveEvent()
    private val connectionManager = JoinConnectionManager.getInstance(application)

    val adapter = HostTeamsWaitingListAdapter()

    val readyButtonShouldBeActive: LiveData<Boolean>
        get() = _readyButtonShouldBeActive

    val teamsJoined: LiveData<List<Team>>
        get() = _teamsJoined

    val startQuizEvent: LiveData<HostedGame>
        get() = _startQuizEvent

    val disconnectedFromHost: LiveEvent<Any>
        get() = _diconnectedFromHost

    val joinCallback = object : JoinConnectionManager.JoinConnectionCallback {

        override fun onConnectionSuccessful(host: String) {
            if (host == hostGame.hostID) {
                connectionManager.host = host
                _readyButtonShouldBeActive.value = true
            }
        }

        override fun onMessageReceived(host: String, message: String) {
            if (message == HostConnectionManager.START) {
                _startQuizEvent.value = hostGame
            } else {
                _teamsJoined.value = parseTeamsFrom(message)
            }
        }

        override fun onDisconnected(host: String) {
            connectionManager.host = null
            _diconnectedFromHost.call()
        }

    }

    init {
        connectionManager.registerCallback(joinCallback)
        requestConnectionFor(hostGame)

        _teamsJoined.value = mutableListOf()
        teamsJoined.observeForever {
            adapter.teams = it
        }
    }

    fun readyButtonClicked() {
        _readyButtonShouldBeActive.value = false
        connectionManager.sendMessageToHost(JoinConnectionManager.READY)
    }

    fun parseTeamsFrom(message: String): List<Team> {
        val teams = mutableListOf<Team>()
        val splittedMessage = message.split(";")
        for (team in splittedMessage) {
            val splittedTeam = team.split(",")
            if (splittedTeam.count() != 3) { continue }
            val teamIsReady = (splittedTeam[2] == "true")
            teams.add(Team(splittedTeam[0], splittedTeam[1], teamIsReady))
        }
        return teams
    }

    private fun requestConnectionFor(hostedGame: HostedGame) {
        connectionManager.requestConnection(hostedGame.hostID, hostedGame.teamName)
    }

    fun disconnectFromHost() {
        connectionManager.disconnectFromHost()
    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(joinCallback)
    }

}