package com.kraci.quicktapquiz.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface QuizGameDao {

    @Query("SELECT q.name AS quiz_name, c.name AS category_name, qs.text AS question_text, qs.hint AS question_hint, qs.image AS question_image, cq.question_value AS question_value, cq.question_bonus AS question_bonus FROM quizzes q JOIN categories c ON q.id = c.quiz_id JOIN category_question cq ON cq.category_id = c.id JOIN questions qs ON cq.question_id = qs.id WHERE q.id = :quizId")
    fun gameForQuiz(quizId: Int): LiveData<List<QuizGameDB>>

    data class QuizGameDB(val quiz_name: String,
                          val category_name: String,
                          val question_text: String,
                          val question_hint: String,
                          val question_image: String,
                          val question_value: Int,
                          val question_bonus: Boolean)

}