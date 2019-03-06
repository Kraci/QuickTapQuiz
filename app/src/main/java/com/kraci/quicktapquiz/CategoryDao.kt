package com.kraci.quicktapquiz

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDao {

    @Insert
    fun insert(category: Category)

    @Query("SELECT * FROM categories WHERE quiz_id = :quizId")
    fun findCategoriesForQuiz(quizId: Int): LiveData<List<Category>>

}