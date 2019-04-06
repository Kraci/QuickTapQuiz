package com.kraci.quicktapquiz

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*

class JoinTeamsWaitingViewModelFactory(private val application: Application, private val param: HostedGame) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return JoinTeamsWaitingViewModel(application, param) as T
    }
}

class JoinTeamsWaitingViewModel(application: Application, param: HostedGame) : AndroidViewModel(application) {

    private val _teamsJoined: MutableLiveData<List<Team>> = MutableLiveData()
    private val _readyButtonShouldBeActive: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _startQuizEvent: MutableLiveData<HostedGame> = MutableLiveData()
    private val connectionManager = JoinConnectionManager.getInstance(application)
    private val hostGame = param

    val adapter = HostTeamsWaitingListAdapter()

    val readyButtonShouldBeActive: LiveData<Boolean>
        get() = _readyButtonShouldBeActive

    val teamsJoined: LiveData<List<Team>>
        get() = _teamsJoined

    val startQuizEvent: LiveData<HostedGame>
        get() = _startQuizEvent

    private var hostID = ""
        set(value) {
            field = value
            if (value == hostGame.hostID) {
                _readyButtonShouldBeActive.value = true
            }
        }

    val joinCallback = object : JoinConnectionManager.JoinConnectionCallback {

        override fun onConnectionSuccessful(host: String) {
            hostID = host
            _readyButtonShouldBeActive.value = true
        }

        override fun onMessageReceived(host: String, message: String) {
            if (message == "START") {
                _startQuizEvent.value = hostGame
            } else {
                _teamsJoined.value = parseTeamsFrom(message)
            }
        }

        override fun onDisconnected(host: String) {
            Toast.makeText(application.applicationContext, "$host LEFT!", Toast.LENGTH_LONG).show()
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
        connectionManager.sendMessage(hostID, "READY")
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

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(joinCallback)
    }

}