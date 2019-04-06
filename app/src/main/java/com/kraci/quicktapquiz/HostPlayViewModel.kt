package com.kraci.quicktapquiz

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.nearby.connection.ConnectionInfo

class HostPlayViewModelFactory(private val application: Application, private val gameAdapter: GameAdapter) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HostPlayViewModel(application, gameAdapter) as T
    }
}

class HostPlayViewModel(application: Application, private val gameAdapter: GameAdapter) : AndroidViewModel(application), HostPlayListAdapter.ClickListener {

    val adapter = HostPlayListAdapter(application.applicationContext)

    private val _hintClicked: MutableLiveData<String> = MutableLiveData()
    private val _questionText: MutableLiveData<String> = MutableLiveData()
    private val _ableVoted: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _answerEvent: LiveEvent<Any> = LiveEvent()
    private val connectionManager = HostConnectionManager.getInstance(application)
    private val answeredTeams = mutableListOf<Team>()

    val hintClicked: LiveData<String>
        get() = _hintClicked

    val questionText: LiveData<String>
        get() = _questionText

    val ableVoted: LiveData<Boolean>
        get() = _ableVoted

    val answerEvent: LiveEvent<Any>
        get() = _answerEvent

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
        val ableVoted = _ableVoted.value
        if (ableVoted != null && ableVoted) {
            _answerEvent.call()
            connectionManager.sendMessage(message = "DISABLE")
        } else {
            _ableVoted.value = true
            connectionManager.sendMessage(message = "RESET")
        }
    }

    fun hintClicked() {
        _hintClicked.value = gameAdapter.hint
    }

    override fun onCorrectClick(team: Team) {
        connectionManager.updateScoreFor(team, gameAdapter.value)
        _answerEvent.call()
        connectionManager.sendMessage(message = "DISABLE")
    }

    override fun onWrongClick(team: Team) {
        connectionManager.updateScoreFor(team, gameAdapter.value * -1)
        adapter.answeringIndex += 1
        adapter.notifyDataSetChanged()
    }

    fun disableVote() {
        connectionManager.sendMessage(message = "DISABLE")
    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(connectionCallback)
    }

}