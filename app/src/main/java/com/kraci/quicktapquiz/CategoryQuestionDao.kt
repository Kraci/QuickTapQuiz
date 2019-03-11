package com.kraci.quicktapquiz

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryQuestionDao {

    @Insert
    fun insert(categoryQuestion: CategoryQuestion)

    @Query(
        """
            SELECT *
            FROM questions q
            JOIN category_question cg ON q.id = cg.question_id
            WHERE cg.category_id = :categoryId""")
    fun findQuestionsForCategory(categoryId: Int): LiveData<List<Question>>

}