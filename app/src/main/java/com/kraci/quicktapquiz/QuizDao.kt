package com.kraci.quicktapquiz

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(quiz: Quiz)

    @Query("SELECT * FROM quiz_table ORDER BY name ASC")
    fun allQuizzes(): LiveData<List<Quiz>>

    @Query("DELETE FROM quiz_table")
    fun deleteAll()

}