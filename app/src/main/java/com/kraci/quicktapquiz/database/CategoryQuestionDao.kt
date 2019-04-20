package com.kraci.quicktapquiz.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryQuestionDao {

    @Insert
    fun insert(categoryQuestion: CategoryQuestion)

    @Query("DELETE FROM category_question")
    fun deleteAll()

}