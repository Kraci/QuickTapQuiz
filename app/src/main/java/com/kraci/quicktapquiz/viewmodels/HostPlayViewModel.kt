package com.kraci.quicktapquiz.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.kraci.quicktapquiz.adapters.HostPlayListAdapter
import com.kraci.quicktapquiz.connections.HostConnectionManager
import com.kraci.quicktapquiz.connections.JoinConnectionManager
import com.kraci.quicktapquiz.utils.LiveEvent

class HostPlayViewModelFactory(private val application: Application, private val gameAdapter: GameAdapter, private val bonus: QuestionGame?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HostPlayViewModel(application, gameAdapter, bonus) as T
    }
}

class HostPlayViewModel(application: Application, private val gameAdapter: GameAdapter, private val bonus: QuestionGame?) : AndroidViewModel(application),
    HostPlayListAdapter.ClickListener {

    val adapter = HostPlayListAdapter(application.applicationContext)

    private val _hintClicked: LiveEvent<String> = LiveEvent()
    private val _questionText: MutableLiveData<String> = MutableLiveData()
    private val _answerEvent: LiveEvent<Any> = LiveEvent()
    private val _ableVoted: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _imageToLoad: MutableLiveData<String> = MutableLiveData()
    private val _imageVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    private var imageLoaded = false
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

    val imageToLoad: LiveData<String>
        get() = _imageToLoad

    val imageVisible: LiveData<Boolean>
        get() = _imageVisible

    val ableVoted: LiveData<Boolean>
        get() = _ableVoted

    val bonusAlert: LiveData<Any>
        get() = _bonusAlert

    val connectionCallback = object : HostConnectionManager.HostConnectionCallback {

        override fun onConnectionInitiated(client: String, connectionInfo: ConnectionInfo) { }

        override fun onConnectionSuccessful(client: String) { }

        override fun onDisconnected(client: String) { }

        override fun onPayloadReceived(client: String, message: String) {
            if (message == JoinConnectionManager.ANSWER) {
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
            disableVote()
            _answerEvent.call()
        } else {
            connectionManager.sendMessage(message = HostConnectionManager.RESET)
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

    fun imageClicked() {
        val visible = _imageVisible.value
        if (visible != null && visible) {
            _imageVisible.value = false
        } else {
            if (!imageLoaded) {
                if (bonusMode && bonus != null) {
                    _imageToLoad.value = bonus.image
                } else {
                    _imageToLoad.value = gameAdapter.image
                }
                imageLoaded = true
            }
            _imageVisible.value = true
        }
    }

    override fun onCorrectClick(team: Team) {
        disableVote()
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
        connectionManager.sendMessage(message = HostConnectionManager.DISABLE)
    }

    override fun onCleared() {
        super.onCleared()
        connectionManager.unregisterCallback(connectionCallback)
    }

}