package com.kraci.quicktapquiz.viewmodels

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.*
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.kraci.quicktapquiz.connections.JoinConnectionManager
import com.kraci.quicktapquiz.adapters.JoinQuizChooseAdapter
import com.kraci.quicktapquiz.utils.LiveEvent
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HostedGame(val hostID: String, var teamName: String, val gameName: String) : Parcelable

class JoinQuizChooseViewModel(application: Application) : AndroidViewModel(application),
    JoinQuizChooseAdapter.ClickListener {

    val adapter = JoinQuizChooseAdapter()

    private val _hostedGames: MutableLiveData<List<HostedGame>> = MutableLiveData()
    private val _hostGamePicked: MutableLiveData<HostedGame> = MutableLiveData()
    private val _emptyNameEvent: LiveEvent<Any> = LiveEvent()
    private val connectionManager = JoinConnectionManager.getInstance(application)
    private var teamName = ""

    private val joinConnectionCallback = object :
        JoinConnectionManager.JoiningConnectionCallback {

        override fun onEndpointFound(host: String, endpointInfo: DiscoveredEndpointInfo) {
            val hostedGamesTmp = _hostedGames.value as? MutableList
            val gameToAdd = HostedGame(host, "", endpointInfo.endpointName)
            var gameAlreadyExist = false

            if (hostedGamesTmp != null) {
                for (game in hostedGamesTmp) {
                    if (game.hostID == gameToAdd.hostID) {
                        gameAlreadyExist = true
                    }
                }
            }

            if (!gameAlreadyExist) {
                hostedGamesTmp?.add(gameToAdd)
                _hostedGames.value = hostedGamesTmp
            }
        }

        override fun onEndpointLost(host: String) {
            val hostedGamesTmp = _hostedGames.value as? MutableList
            var hostedGameToRemove: HostedGame? = null
            if (hostedGamesTmp != null) {
                for (game in hostedGamesTmp) {
                    if (game.hostID == host) {
                        hostedGameToRemove = game
                        break
                    }
                }
                if (hostedGameToRemove != null) {
                    hostedGamesTmp.remove(hostedGameToRemove)
                    _hostedGames.value = hostedGamesTmp
                }
            }
        }

    }

    init {
        connectionManager.registerCallback(joinConnectionCallback)
        connectionManager.startDiscovery()

        adapter.clickListener = this
        _hostedGames.value = mutableListOf()
        hostedGames.observeForever {
            adapter.hostedGames = it
        }
    }

    val hostedGames: LiveData<List<HostedGame>>
        get() = _hostedGames

    val hostGamePicked: LiveData<HostedGame>
        get() = _hostGamePicked

    val emptyNameEvent: LiveData<Any>
        get() = _emptyNameEvent

    fun onTeamNameChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        teamName = s.toString()
    }

    override fun onItemClick(hostedGame: HostedGame) {
        if (teamName.isEmpty()) {
            _emptyNameEvent.call()
            return
        }
        stopDiscovery()
        hostedGame.teamName = teamName
        _hostGamePicked.value = hostedGame
    }

    fun stopDiscovery() {
        connectionManager.stopDiscovery()
    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(joinConnectionCallback)
    }

}