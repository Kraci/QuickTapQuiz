package com.kraci.quicktapquiz

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.*
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Game(val hostID: String, var teamName: String, val gameName: String) : Parcelable

class JoinQuizChooseViewModel(application: Application) : AndroidViewModel(application), JoinQuizChooseAdapter.ClickListener {

    val adapter = JoinQuizChooseAdapter()

    private val _hostedGames: MutableLiveData<List<Game>> = MutableLiveData()
    private val _hostGamePicked: MutableLiveData<Game> = MutableLiveData()
    private val _emptyNameEvent: LiveEvent<Any> = LiveEvent()
    private val connectionManager = JoinConnectionManager.getInstance(application)
    private var teamName = ""

    private val joinConnectionCallback = object : JoinConnectionManager.JoiningConnectionCallback {

        override fun onEndpointFound(host: String, endpointInfo: DiscoveredEndpointInfo) {
            val hostedGamesTmp = _hostedGames.value as? MutableList
            val gameToAdd = Game(host, "", endpointInfo.endpointName)
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
            var gameToRemove: Game? = null
            if (hostedGamesTmp != null) {
                for (game in hostedGamesTmp) {
                    if (game.hostID == host) {
                        gameToRemove = game
                        break
                    }
                }
                if (gameToRemove != null) {
                    hostedGamesTmp.remove(gameToRemove)
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

    val hostedGames: LiveData<List<Game>>
        get() = _hostedGames

    val hostGamePicked: LiveData<Game>
        get() = _hostGamePicked

    val emptyNameEvent: LiveData<Any>
        get() = _emptyNameEvent

    fun onTeamNameChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        teamName = s.toString()
    }

    fun stopDiscovery() {
        connectionManager.stopDiscovery()
    }

    override fun onItemClick(hostedGame: Game) {
        if (teamName.isEmpty()) {
            _emptyNameEvent.call()
            return
        }
        hostedGame.teamName = teamName
        _hostGamePicked.value = hostedGame
    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(joinConnectionCallback)
    }

}