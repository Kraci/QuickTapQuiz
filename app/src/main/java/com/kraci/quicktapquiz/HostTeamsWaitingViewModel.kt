package com.kraci.quicktapquiz

import android.app.Application
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.gms.nearby.connection.*
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.text.Charsets.UTF_8

data class Team(val deviceID: String, val teamName: String, var isReady: Boolean = false, var score: Int = 0)

@Parcelize
data class QuestionGame(val text: String, val hint: String, val image: String, val value: Int, val bonus: Boolean) : Parcelable

@Parcelize
data class CategoryGame(val name: String, var questions: @RawValue List<QuestionGame>) : Parcelable

@Parcelize
data class QuizGame(val name: String, val categories: @RawValue List<CategoryGame>) : Parcelable

class HostTeamsWaitingViewModelFactory(private val application: Application, private val quizInfo: QuizInfo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HostTeamsWaitingViewModel(application, quizInfo) as T
    }
}

class HostTeamsWaitingViewModel(application: Application, quizInfo: QuizInfo) : AndroidViewModel(application) {

    private val _teamsJoined: MutableLiveData<ArrayList<Team>> = MutableLiveData()
    // Ulozenie timov, kym niesu prepojeni. V metode o uspesnom pripojeni mi nepride meno timu, iba device ID.
    // Nemozem hned tim pridat do teamsJoined, pretoze vtedy by klienti este nepocuvali na spravy
    private var acceptedTeams: ArrayList<Team> = ArrayList()
    private val _startQuizButton: LiveEvent<Any> = LiveEvent()
    private val _startQuizButtonShouldBeActive: MutableLiveData<Boolean> = MutableLiveData()
    private val connectionManager = HostConnectionManager.getInstance(application)

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository: QuizRepository

    val adapter = HostTeamsWaitingListAdapter()

    var quizGame: QuizGame? = null

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

        override fun onDisconnected(client: String) {
            Toast.makeText(application.applicationContext, "$client LEFT!", Toast.LENGTH_LONG).show()
        }

    }

    init {
        connectionManager.registerCallback(callback)
        connectionManager.startAdvertise(quizInfo.name)

        val db = QuizDatabase.getDatabase(application, scope)
        repository = QuizRepository(db.quizDao(), db.categoryDao(), db.questionDao(), db.categoryQuestionDao(), db.quizGameDao())

        repository.quizGame(quizInfo.id).observeForever {
            quizGame = parseQuizGame(it)
        }

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
                //allAreReady = false // FIXME: uncomment
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
        connectionManager.stopAdvertise()
        val teams = _teamsJoined.value
        if (teams != null) {
            connectionManager.teams = teams.toMutableList()
        }
        connectionManager.sendMessage(message = "START")
        _startQuizButton.call()
    }

    private fun parseQuizGame(quizGameDB: List<QuizGameDao.QuizGameDB>): QuizGame? {
        if (quizGameDB.count() < 1) { return null }
        val quizGameName = quizGameDB[0].quiz_name
        val categories = mutableListOf<CategoryGame>()
        var actualCategory = CategoryGame(quizGameDB[0].category_name, mutableListOf())
        var actualQuestions = mutableListOf<QuestionGame>()

        for (quizGameRow in quizGameDB) {
            if (actualCategory.name != quizGameRow.category_name) {
                actualCategory.questions = actualQuestions
                categories.add(actualCategory)
                actualCategory = CategoryGame(quizGameRow.category_name, mutableListOf())
                actualQuestions = mutableListOf()
            }
            val question = QuestionGame(quizGameRow.question_text, quizGameRow.question_hint, quizGameRow.question_image, quizGameRow.question_value, quizGameRow.question_bonus)
            actualQuestions.add(question)
        }

        actualCategory.questions = actualQuestions
        categories.add(actualCategory)

        return QuizGame(quizGameName, categories)
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

//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestroy() {
//        Nearby.getConnectionsClient(_application.applicationContext).stopAdvertising()
//    }