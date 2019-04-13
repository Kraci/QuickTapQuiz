package com.kraci.quicktapquiz.viewmodels

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.*
import com.kraci.quicktapquiz.connections.HostConnectionManager
import com.kraci.quicktapquiz.adapters.HostPlayQuestionsListAdapter
import kotlinx.android.parcel.Parcelize

enum class GameAdapterType {
    CATEGORY, QUESTION
}

@Parcelize
data class GameAdapter(val type: GameAdapterType, val text: String, val hint: String, val image: String, val value: Int, val bonus: Boolean, val category: String = "") : Parcelable

class HostPlayQuestionsViewModelFactory(private val application: Application, private val quizGame: QuizGame) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HostPlayQuestionsViewModel(application, quizGame) as T
    }
}

class HostPlayQuestionsViewModel(application: Application, quizGame: QuizGame): AndroidViewModel(application),
    HostPlayQuestionsListAdapter.ClickListener {

    private val _questionChoosed: MutableLiveData<GameAdapter> = MutableLiveData()
    private val connectionManager = HostConnectionManager.getInstance(application)
    private val bonuses = mutableMapOf<String, QuestionGame>()
    private var choosedQuestionIndex = -1

    var bonus: QuestionGame? = null

    val questionChoosed: LiveData<GameAdapter>
        get() = _questionChoosed

    val adapter = HostPlayQuestionsListAdapter()

    init {
        val gameAdapter = mutableListOf<GameAdapter>()
        for (category in quizGame.categories) {
            gameAdapter.add(
                GameAdapter(
                    GameAdapterType.CATEGORY,
                    category.name,
                    "",
                    "",
                    0,
                    false
                )
            )
            for (question in category.questions) {
                if (question.bonus) {
                    bonuses.put(category.name, question)
                } else {
                    gameAdapter.add(
                        GameAdapter(
                            GameAdapterType.QUESTION,
                            question.text,
                            question.hint,
                            question.image,
                            question.value,
                            question.bonus,
                            category.name
                        )
                    )
                }
            }
        }
        adapter.items = gameAdapter
        adapter.clickListener = this
    }

    override fun onQuestionClick(question: GameAdapter, position: Int) {
        choosedQuestionIndex = position

        val items = adapter.items
        val sameCategoryItems = items.count { it.category == question.category }
        if (sameCategoryItems == 1) {
            bonus = bonuses.get(question.category)
        }

        _questionChoosed.value = question
    }

    fun teamScores(): Array<CharSequence> {
        val teams = connectionManager.teams
        teams.sortByDescending { it.score }
        val scores = arrayListOf<String>()
        for (team in teams) {
            val score = "${team.teamName}    ${team.score}"
            scores.add(score)
        }
        return scores.toTypedArray()
    }

    fun updateScore(score: Int, position: Int) {
        val teams = connectionManager.teams
        teams.sortByDescending { it.score }
        teams[position].score = score
        connectionManager.teams = teams
    }

    fun updateQuestionsAfterAnswer() {
        val items = adapter.items.toMutableList()
        items.removeAt(choosedQuestionIndex)
        val filteredEmptyCategories = items.filterIndexed { index, item ->
            if (item.type == GameAdapterType.CATEGORY) {
                if (index + 1 < items.size && items[index + 1].type != GameAdapterType.QUESTION) {
                    return@filterIndexed false
                }
                if (index + 1 == items.size) {
                    return@filterIndexed false
                }
            }
            return@filterIndexed true
        }
        adapter.items = filteredEmptyCategories
    }

    fun stopAllClients() {
        connectionManager.stopAllClients()
    }

}