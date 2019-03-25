package com.kraci.quicktapquiz

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.nearby.connection.*
import java.util.*
import kotlin.text.Charsets.UTF_8

data class Team(val deviceID: String, val teamName: String, var isReady: Boolean = false)

class HostTeamsWaitingViewModelFactory(private val application: Application, private val param: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HostTeamsWaitingViewModel(application, param) as T
    }
}

class HostTeamsWaitingViewModel(application: Application, param: String) : AndroidViewModel(application) {

    private val _teamsJoined: MutableLiveData<ArrayList<Team>> = MutableLiveData()
    // Ulozenie timov, kym niesu prepojeni. V metode o uspesnom pripojeni mi nepride meno timu, iba device ID.
    // Nemozem hned tim pridat do teamsJoined, pretoze vtedy by klienti este nepocuvali na spravy
    private var acceptedTeams: ArrayList<Team> = ArrayList()
    private val _startQuizButton: LiveEvent<Any> = LiveEvent()
    private val _startQuizButtonShouldBeActive: MutableLiveData<Boolean> = MutableLiveData(false)
    private val connectionManager = HostConnectionManager.getInstance(application)
    private var hostQuizName = param

    val adapter = HostTeamsWaitingListAdapter()

    val startQuizButtonShouldBeActive: LiveData<Boolean>
        get() = _startQuizButtonShouldBeActive

    val startQuizButton: LiveData<Any>
        get() = _startQuizButton

    val teamsJoined: LiveData<ArrayList<Team>>
        get() = _teamsJoined

    val callback = object : HostConnectionManager.HostConnectionCallback {

        override fun onPayloadReceived(client: String, message: String) {
            if (message == "READY") {
                val teams = _teamsJoined.value
                if (teams != null) {
                    for (team in teams) {
                        if (team.deviceID == client) {
                            team.isReady = true
                            break
                        }
                    }
                    adapter.teams = teams

                    var allAreReady = true
                    for (team in teams) {
                        if (!team.isReady) {
                            allAreReady = false
                            break
                        }
                    }
                    _startQuizButtonShouldBeActive.value = allAreReady

                    var data = ""
                    val clients = mutableListOf<String>()
                    for (team in teams) {
                        clients.add(team.deviceID)
                        val teamData = "${team.deviceID},${team.teamName},${team.isReady};"
                        data += teamData
                    }
                    if (!data.isEmpty()) {
                        data = data.substring(0, data.length - 1)
                    }

                    connectionManager.sendMessage(clients, data)
                }
            }
        }

        override fun onConnectionInitiated(client: String, connectionInfo: ConnectionInfo) {
            acceptedTeams.add(Team(client, connectionInfo.endpointName))
        }

        override fun onConnectionSuccessful(client: String) {
            var foundTeam: Team? = null
            for (team in acceptedTeams) {
                if (team.deviceID == client) {
                    foundTeam = team
                    break
                }
            }
            if (foundTeam != null) {
                acceptedTeams.remove(foundTeam)
                val teams = _teamsJoined.value
                teams?.add(foundTeam)
                _teamsJoined.value = teams
            }
        }

        override fun onDisconnected(client: String) { }

    }

    init {
        connectionManager.registerCallback(callback)
        connectionManager.startAdvertise(hostQuizName)

        _teamsJoined.value = ArrayList()
        teamsJoined.observeForever {
            adapter.teams = it

            var allAreReady = true
            for (team in it) {
                if (!team.isReady) {
                    allAreReady = false
                    break
                }
            }
            if (it.isEmpty()) {
                allAreReady = false
            }
            _startQuizButtonShouldBeActive.value = allAreReady

            var data = ""
            val clients = mutableListOf<String>()
            for (team in it) {
                clients.add(team.deviceID)
                val teamData = "${team.deviceID},${team.teamName},${team.isReady};"
                data += teamData
            }
            if (!data.isEmpty()) {
                data = data.substring(0, data.length - 1)
            }

            connectionManager.sendMessage(clients, data)
        }
    }

    fun startQuizTapped() {
        val clients = mutableListOf<String>()
        val teams = _teamsJoined.value
        teams?.forEach { team -> clients.add(team.deviceID) }
        connectionManager.sendMessage(clients, "START")
        _startQuizButton.call()
    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(callback)
    }

}

//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun onCreate() {
//        startAdvertise()
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroy() {
//        Nearby.getConnectionsClient(_application.applicationContext).stopAdvertising()
//    }