package com.kraci.quicktapquiz

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.util.*
import kotlin.text.Charsets.UTF_8

data class Team(val deviceID: String, val teamName: String, var isReady: Boolean = false)

class HostTeamsWaitingViewModel(application: Application): AndroidViewModel(application), LifecycleObserver {

    private val _teamsJoined: MutableLiveData<ArrayList<Team>> = MutableLiveData()
    private val _startQuizButton: LiveEvent<Any> = LiveEvent()
    private val _application: Application = application
    private var acceptedTeams: ArrayList<Team> = ArrayList()

    val startQuizButtonShouldBeActive = ObservableField(false)
    val adapter = HostTeamsWaitingListAdapter()
    var hostQuizName = "HOST"

    init {
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
            startQuizButtonShouldBeActive.set(allAreReady)

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

            Nearby.getConnectionsClient(_application.applicationContext)
                .sendPayload(clients, Payload.fromBytes(data.toByteArray(UTF_8)))
        }
    }

    val startQuizButton: LiveData<Any>
        get() = _startQuizButton

    val teamsJoined: LiveData<ArrayList<Team>>
        get() = _teamsJoined

    fun startQuizTapped() {
        _startQuizButton.call()
    }

    // NETWORKING

    val advertisingOption = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()

    val payloadCallback = object : PayloadCallback() {

        override fun onPayloadReceived(p0: String, p1: Payload) {
            p1.asBytes()?.let {
                val message = String(it, UTF_8)
                if (message == "READY") {
                    val teams = _teamsJoined.value
                    if (teams != null) {
                        for (team in teams) {
                            if (team.deviceID == p0) {
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
                        startQuizButtonShouldBeActive.set(allAreReady)

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

                        Nearby.getConnectionsClient(_application.applicationContext)
                            .sendPayload(clients, Payload.fromBytes(data.toByteArray(UTF_8)))
                    }
                }
            }
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            println("Payload transfer update")
        }

    }

    val connectionCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
            println("CONNECTION CALLBACK: INITIATED")
            println("P0: $p0 | P1_NAME: ${p1.endpointName} | P1_AUTH: ${p1.authenticationToken}")
            acceptedTeams.add(Team(p0, p1.endpointName))
            println("ACCEPTED TEAMS: $acceptedTeams")
            Nearby.getConnectionsClient(_application.applicationContext)
                .acceptConnection(p0, payloadCallback)
        }

        override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
            println("CONNECTION CALLBACK: RESULT p0 -> $p1")
            when (p1.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    var foundTeam: Team? = null
                    for (team in acceptedTeams) {
                        if (team.deviceID == p0) {
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
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> println("FATAL (need handle) -> STATUS: REJECTED")
                ConnectionsStatusCodes.STATUS_ERROR -> println("FATAL (need handle) -> STATUS: ERROR")
            }
        }

        override fun onDisconnected(p0: String) {
            println("CONNECTION CALLBACK: DISCONNECT")
        }

    }

    fun startAdvertise() {

        Nearby.getConnectionsClient(_application.applicationContext)
            .startAdvertising(
                hostQuizName,
                "com.kraci.quicktapquiz",
                connectionCallback,
                advertisingOption
            )
            .addOnSuccessListener {
                println("ADVERTISING!")
            }
            .addOnFailureListener {
                // it.printStackTrace()
                println("ADVERTISE FAILURE: $it")
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

}