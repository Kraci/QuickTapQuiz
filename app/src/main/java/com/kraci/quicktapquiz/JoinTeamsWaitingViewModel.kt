package com.kraci.quicktapquiz

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlin.text.Charsets.UTF_8

class JoinTeamsWaitingViewModel(application: Application) : AndroidViewModel(application) {

    val adapter = HostTeamsWaitingListAdapter()
    val readyButtonShouldBeActive = ObservableField(false)

    private val _teamsJoined: MutableLiveData<List<Team>> = MutableLiveData()
    private val _application = application

    val teamsJoined: LiveData<List<Team>>
        get() = _teamsJoined

    var hostGame: Game? = null
        set(value) {
            if (value != null) {
                field = value
                requestConnectionFor(value)
            }
        }

    private var hostID = ""
        set(value) {
            field = value
            if (value == hostGame?.hostID) {
                readyButtonShouldBeActive.set(true)
            }
        }

    init {
        _teamsJoined.value = mutableListOf()
        teamsJoined.observeForever {
            adapter.teams = it
        }
    }

    fun readyButtonClicked() {
        readyButtonShouldBeActive.set(false)
        Nearby.getConnectionsClient(_application.applicationContext)
            .sendPayload(hostID, Payload.fromBytes("READY".toByteArray(UTF_8)))
    }

    fun parseTeamsFrom(message: String): List<Team> {
        val teams = mutableListOf<Team>()
        val splittedMessage = message.split(";")
        for (team in splittedMessage) {
            val splittedTeam = team.split(",")
            val teamIsReady = (splittedTeam[2] == "true")
            teams.add(Team(splittedTeam[0], splittedTeam[1], teamIsReady))
        }
        return teams
    }

    fun requestConnectionFor(game: Game) {

        val payloadCallback = object : PayloadCallback() {

            override fun onPayloadReceived(p0: String, p1: Payload) {
                p1.asBytes()?.let {
                    val message = String(it, UTF_8)
                    _teamsJoined.value = parseTeamsFrom(message)
                }
            }

            override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) { }

        }

        val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {

            override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
                Nearby.getConnectionsClient(_application.applicationContext)
                    .acceptConnection(p0, payloadCallback)
            }

            override fun onConnectionResult(p0: String, p1: ConnectionResolution) {
                when (p1.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> hostID = p0
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> println("STATUS: REJECTED")
                    ConnectionsStatusCodes.STATUS_ERROR -> println("STATUS: ERROR")
                }
            }

            override fun onDisconnected(p0: String) {
                println("STATUS: onDisconnected")
            }

        }

        Nearby.getConnectionsClient(_application.applicationContext)
            .requestConnection(
                game.teamName,
                game.hostID,
                connectionLifecycleCallback
            )

    }

}