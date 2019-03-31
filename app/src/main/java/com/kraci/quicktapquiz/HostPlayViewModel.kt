package com.kraci.quicktapquiz

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.nearby.connection.ConnectionInfo

class HostPlayViewModelFactory(private val application: Application, private val gameAdapter: GameAdapter) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HostPlayViewModel(application, gameAdapter) as T
    }
}

class HostPlayViewModel(application: Application, gameAdapter: GameAdapter) : AndroidViewModel(application), HostPlayListAdapter.ClickListener {

    val adapter = HostPlayListAdapter()

    private val _gameAdapter = gameAdapter
    private val _hintClicked: MutableLiveData<String> = MutableLiveData()
    private val _questionText: MutableLiveData<String> = MutableLiveData()
    private val _ableVoteEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    private val connectionManager = HostConnectionManager.getInstance(application)
    private val answeredTeams = mutableListOf<Team>()

    val hintClicked: LiveData<String>
        get() = _hintClicked

    val questionText: LiveData<String>
        get() = _questionText

    val ableVoteEnabled: LiveData<Boolean>
        get() = _ableVoteEnabled

    val connectionCallback = object : HostConnectionManager.HostConnectionCallback {

        override fun onConnectionInitiated(client: String, connectionInfo: ConnectionInfo) { }

        override fun onConnectionSuccessful(client: String) { }

        override fun onDisconnected(client: String) { }

        override fun onPayloadReceived(client: String, message: String) {
            if (message == "ANSWER") {
                val team = connectionManager.teamFor(client)
                if (team != null) {
                    answeredTeams.add(team)
                    adapter.teams = answeredTeams
                }
            }
        }

    }

    init {
        connectionManager.registerCallback(connectionCallback)
        adapter.clickListener = this
        _questionText.value = gameAdapter.text
    }

    fun ableVoteClicked() {
        _ableVoteEnabled.value = false
        connectionManager.sendMessage(message = "RESET")
    }

    fun hintClicked() {
        _hintClicked.value = _gameAdapter.hint
    }

    override fun onCorrectClick(team: Team) {

    }

    override fun onWrongClick(team: Team) {

    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(connectionCallback)
    }

}