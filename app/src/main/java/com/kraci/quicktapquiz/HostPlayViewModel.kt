package com.kraci.quicktapquiz

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.nearby.connection.ConnectionInfo

class HostPlayViewModelFactory(private val application: Application, private val gameAdapter: GameAdapter, private val bonus: QuestionGame?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HostPlayViewModel(application, gameAdapter, bonus) as T
    }
}

class HostPlayViewModel(application: Application, private val gameAdapter: GameAdapter, private val bonus: QuestionGame?) : AndroidViewModel(application), HostPlayListAdapter.ClickListener {

    val adapter = HostPlayListAdapter(application.applicationContext)

    private val _hintClicked: MutableLiveData<String> = MutableLiveData()
    private val _questionText: MutableLiveData<String> = MutableLiveData()
    private val _answerEvent: LiveEvent<Any> = LiveEvent()
    private val _ableVoted: MutableLiveData<Boolean> = MutableLiveData(false)
    private val connectionManager = HostConnectionManager.getInstance(application)
    private val answeredTeams = mutableListOf<Team>()
    private val _bonusAlert: LiveEvent<Any> = LiveEvent()
    private var bonusMode = false

    val hintClicked: LiveData<String>
        get() = _hintClicked

    val questionText: LiveData<String>
        get() = _questionText

    val answerEvent: LiveData<Any>
        get() = _answerEvent

    val ableVoted: LiveData<Boolean>
        get() = _ableVoted

    val bonusAlert: LiveData<Any>
        get() = _bonusAlert

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
        val voted = _ableVoted.value
        if (voted != null && voted) {
            connectionManager.sendMessage(message = "DISABLE")
            _answerEvent.call()
        } else {
            connectionManager.sendMessage(message = "RESET")
            _ableVoted.value = true
        }
    }

    fun hintClicked() {
        if (bonusMode && bonus != null) {
            _hintClicked.value = bonus.hint
        } else {
            _hintClicked.value = gameAdapter.hint
        }
    }

    override fun onCorrectClick(team: Team) {
        connectionManager.sendMessage(message = "DISABLE")
        if (bonus != null) {
            if (bonusMode) {
                connectionManager.updateScoreFor(team, bonus.value)
                _answerEvent.call()
            } else {
                connectionManager.updateScoreFor(team, gameAdapter.value)
                _questionText.value = bonus.text
                adapter.answeringIndex = 0
                adapter.teams = listOf(team)
                bonusMode = true
                _bonusAlert.call()
            }
        } else {
            connectionManager.updateScoreFor(team, gameAdapter.value)
            _answerEvent.call()
        }
    }

    override fun onWrongClick(team: Team) {
        if (bonusMode) {
            _answerEvent.call()
        } else {
            connectionManager.updateScoreFor(team, gameAdapter.value * -1)
            adapter.answeringIndex += 1
            adapter.notifyDataSetChanged()
        }
    }

    fun disableVote() {
        connectionManager.sendMessage(message = "DISABLE")
    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(connectionCallback)
    }

}