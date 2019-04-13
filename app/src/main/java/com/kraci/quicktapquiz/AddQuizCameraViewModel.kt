package com.kraci.quicktapquiz

import android.app.Application
import android.os.StrictMode
import androidx.lifecycle.AndroidViewModel
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import org.json.JSONException
import java.lang.Exception
import java.net.URL
import kotlin.coroutines.experimental.CoroutineContext

class QuizJSON(val name: String, val categories: List<CategoriesJSON>)
class CategoriesJSON(val name: String, val questions: List<QuestionJSON>)
class QuestionJSON(val text: String, val hint: String, val image: String, val value: String, val bonus: String)
class ResponseJSON(val status: Boolean, val message: QuizJSON)

class AddQuizCameraViewModel(application: Application): AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository: QuizRepository

    init {
        val db = QuizDatabase.getDatabase(application, scope)
        repository = QuizRepository(db.quizDao(), db.categoryDao(), db.questionDao(), db.categoryQuestionDao(), db.quizGameDao())
        request()
    }

    fun request() = scope.launch(Dispatchers.IO) {
        val text = URL("http://quicktapquiz.codelabs.sk/api/quiz.php?code=1").readText()
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
            addQuiz(responseJSON.message)
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