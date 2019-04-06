package com.kraci.quicktapquiz

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(quiz: Quiz): Long

    @Query("SELECT * FROM quizzes ORDER BY name ASC")
    fun allQuizzes(): LiveData<List<Quiz>>

    @Delete
    fun delete(quiz: Quiz)

    @Query("DELETE FROM quizzes")
    fun deleteAll()

}