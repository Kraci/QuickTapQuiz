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

    override fun onQuestionClick(question: GameAdapter) {
        _questionChoosed.value = question
    }

}