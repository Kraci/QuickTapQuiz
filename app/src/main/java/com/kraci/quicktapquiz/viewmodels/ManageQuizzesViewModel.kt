package com.kraci.quicktapquiz.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.beust.klaxon.Klaxon
import com.kraci.quicktapquiz.adapters.ManageQuizzesListAdapter
import com.kraci.quicktapquiz.database.*
import com.kraci.quicktapquiz.utils.LiveEvent
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import java.net.URL
import kotlin.coroutines.experimental.CoroutineContext

class QuizJSON(val name: String, val categories: List<CategoriesJSON>)
class CategoriesJSON(val name: String, val questions: List<QuestionJSON>)
class QuestionJSON(val text: String, val hint: String, val image: String, val value: String, val bonus: String)
class ResponseJSON(val status: Boolean, val message: QuizJSON)

class ManageQuizzesViewModel(application: Application): AndroidViewModel(application),
    ManageQuizzesListAdapter.ClickListener {

    val adapter = ManageQuizzesListAdapter()
    val allQuizzes: LiveData<List<Quiz>>

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository: QuizRepository

    private val _addQuizEvent: LiveEvent<Any> = LiveEvent()
    private val _notValidCodeEvent: LiveEvent<Any> =
        LiveEvent()

    val addQuizEvent: LiveData<Any>
        get() = _addQuizEvent

    val notValidCodeEvent: LiveData<Any>
        get() = _notValidCodeEvent

    init {
        val db = QuizDatabase.getDatabase(application, scope)
        repository = QuizRepository(
            db.quizDao(),
            db.categoryDao(),
            db.questionDao(),
            db.categoryQuestionDao(),
            db.quizGameDao()
        )
        allQuizzes = repository.allQuizzes
        allQuizzes.observeForever {
            adapter.quizzes = it
        }
        adapter.clickListener = this
    }

    fun addQuizClicked() {
        _addQuizEvent.call()
    }

    override fun onDeleteItemClick(quiz: Quiz) {
        delete(quiz)
    }

    fun delete(quiz: Quiz) = scope.launch(Dispatchers.IO) {
        repository.delete(quiz)
    }

    fun request(code: Int) = scope.launch(Dispatchers.IO) {
        val text = URL("http://quicktapquiz.codelabs.sk/api/quiz.php?code=$code").readText()
        saveParsedJSONtoDB(text)

//        try {
//            val text = URL("http://quicktapquiz.codelabs.sk/api/quiz.php?code=1").readText()
//            saveParsedJSONtoDB(text)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    private fun saveParsedJSONtoDB(json: String) {
        val responseJSON = parseJSON(json)
        if (responseJSON != null) {
            if (responseJSON.status) {
                addQuiz(responseJSON.message)
            } else {
                scope.launch(context = Dispatchers.Main) {
                    _notValidCodeEvent.call()
                }
            }
        }
    }

    private fun parseJSON(json: String): ResponseJSON? {
        return Klaxon().parse<ResponseJSON>(json)
    }

    private fun addQuiz(quizJSON: QuizJSON) = scope.launch(Dispatchers.IO) {
        val quiz = Quiz(name = quizJSON.name)
        val quizID = repository.insert(quiz)

        for (categoryJSON in quizJSON.categories) {
            val category = Category(name = categoryJSON.name, quizId = quizID.toInt())
            val categoryID = repository.insert(category)

            for (questionJSON in categoryJSON.questions) {
                val question = Question(
                    text = questionJSON.text,
                    hint = questionJSON.hint,
                    image = questionJSON.image
                )
                val questionID = repository.insert(question)

                val value = questionJSON.value.toInt()
                val bonus = questionJSON.bonus == "true"

                val categoryQuestion =
                    CategoryQuestion(
                        categoryID.toInt(),
                        questionID.toInt(),
                        value,
                        bonus
                    )
                repository.insert(categoryQuestion)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}