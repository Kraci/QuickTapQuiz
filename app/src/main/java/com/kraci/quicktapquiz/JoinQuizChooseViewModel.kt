package com.kraci.quicktapquiz

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Strategy

data class Game(val hostID: String, var teamName: String, val gameName: String)

class JoinQuizChooseViewModel(application: Application) : AndroidViewModel(application), JoinQuizChooseAdapter.ClickListener, LifecycleObserver {

    val adapter = JoinQuizChooseAdapter()

    private val _hostedGames: MutableLiveData<ArrayList<Game>> = MutableLiveData()
    private val _hostGamePicked: MutableLiveData<Game> = MutableLiveData()
    private val _emptyNameEvent: LiveEvent<Any> = LiveEvent()
    private val _application: Application = application
    private var teamName = ""

    init {
        adapter.clickListener = this
        _hostedGames.value = ArrayList()
    }

    val hostedGames: LiveData<ArrayList<Game>>
        get() = _hostedGames

    val hostGamePicked: LiveData<Game>
        get() = _hostGamePicked

    val emptyNameEvent: LiveData<Any>
        get() = _emptyNameEvent

    fun onTeamNameChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        teamName = s.toString()
    }

    override fun onItemClick(hostedGame: Game) {
        if (teamName.isEmpty()) {
            _emptyNameEvent.call()
            return
        }
        hostedGame.teamName = teamName
        _hostGamePicked.value = hostedGame
    }

    fun startDiscovery() {

        val discoveryOption = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        val discoveryCallback = object : EndpointDiscoveryCallback() {

            override fun onEndpointFound(p0: String, p1: DiscoveredEndpointInfo) {
                // TODO: Not tested with multiple connections
                val hostedGamesTmp = _hostedGames.value
                val gameToAdd = Game(p0, "", p1.endpointName)
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

            override fun onEndpointLost(p0: String) {
                // TODO: Not tested with multiple connections
                println("Endpoint Lost")
                val hostedGamesTmp = _hostedGames.value
                var gameToRemove: Game? = null
                if (hostedGamesTmp != null) {
                    for (game in hostedGamesTmp) {
                        if (game.hostID == p0) {
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

        Nearby.getConnectionsClient(_application)
            .startDiscovery(
                "com.kraci.quicktapquiz",
                discoveryCallback,
                discoveryOption
            )
            .addOnSuccessListener {
                println("DISCOVERING")
            }
            .addOnFailureListener {
                // TODO: pri inite zobrazit view kde pise ze hlada vytvorene hry a ak toto nastane, vypisat error
                println("DISCOVERY FAILED: $it")
            }
    }

}