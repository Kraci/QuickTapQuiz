package com.kraci.quicktapquiz

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.*
import kotlinx.android.parcel.Parcelize

enum class GameAdapterType {
    CATEGORY, QUESTION
}

@Parcelize
data class GameAdapter(val type: GameAdapterType, val text: String, val hint: String, val image: String, val value: Int, val bonus: Boolean, val answered: Boolean) : Parcelable

class HostPlayQuestionsViewModelFactory(private val application: Application, private val quizGame: QuizGame) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HostPlayQuestionsViewModel(application, quizGame) as T
    }
}

class HostPlayQuestionsViewModel(application: Application, quizGame: QuizGame): AndroidViewModel(application), HostPlayQuestionsListAdapter.ClickListener {

    private val _questionChoosed: MutableLiveData<GameAdapter> = MutableLiveData()
    private val connectionManager = HostConnectionManager.getInstance(application)

    private var choosedQuestionIndex = -1

    val questionChoosed: LiveData<GameAdapter>
        get() = _questionChoosed

    val adapter = HostPlayQuestionsListAdapter()

    init {
        val gameAdapter = mutableListOf<GameAdapter>()
        for (category in quizGame.categories) {
            gameAdapter.add(GameAdapter(GameAdapterType.CATEGORY, category.name, "", "", 0, false, false))
            for (question in category.questions) {
                gameAdapter.add(GameAdapter(GameAdapterType.QUESTION, question.text, question.hint, question.image, question.value, question.bonus, question.answered))
            }
        }
        adapter.items = gameAdapter
        adapter.clickListener = this
    }

    override fun onQuestionClick(question: GameAdapter, position: Int) {
        choosedQuestionIndex = position
        _questionChoosed.value = question
    }

    fun teamScores(): Array<CharSequence> {
        val teams = connectionManager.teams
        teams.sortByDescending { it.score }
        val scores = arrayListOf<String>()
        for (team in teams) {
            val score = "${team.teamName} - ${team.score}"
            scores.add(score)
        }
        return scores.toTypedArray()
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
        adapter.notifyDataSetChanged()
    }

}