package com.kraci.quicktapquiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import kotlin.coroutines.experimental.CoroutineContext

class QuizJSON(val name: String, val categories: List<CategoriesJSON>)
class CategoriesJSON(val name: String, val questions: List<QuestionJSON>)
class QuestionJSON(val text: String, val hint: String, val image: String, val value: String, val bonus: String)

class AddQuizCameraViewModel(application: Application): AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository: QuizRepository

    init {
        val db = QuizDatabase.getDatabase(application, scope)
        repository = QuizRepository(db.quizDao(), db.categoryDao(), db.questionDao(), db.categoryQuestionDao(), db.quizGameDao())
    }

    fun saveParsedJSONtoDB(json: String) {
        val quizJSON = parseJSON(json)
        if (quizJSON != null) {
            addQuiz(quizJSON)
        }
    }

    private fun parseJSON(json: String): QuizJSON? {
        return Klaxon().parse<QuizJSON>(json)
    }

    private fun addQuiz(quizJSON: QuizJSON) = scope.launch(Dispatchers.IO) {
        val quiz = Quiz(name = quizJSON.name)
        val quizID = repository.insert(quiz)

        for (categoryJSON in quizJSON.categories) {
            val category = Category(name = categoryJSON.name, quizId = quizID.toInt())
            val categoryID = repository.insert(category)

            for (questionJSON in categoryJSON.questions) {
                val question = Question(text = questionJSON.text, hint = questionJSON.hint, image = questionJSON.image)
                val questionID = repository.insert(question)

                val value = questionJSON.value.toInt()
                val bonus = questionJSON.bonus == "true"

                val categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), value, bonus)
                repository.insert(categoryQuestion)
            }
        }
    }

}