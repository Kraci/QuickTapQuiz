package com.kraci.quicktapquiz

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(quiz: Quiz)

    @Query("SELECT * FROM quizzes ORDER BY name ASC")
    fun allQuizzes(): LiveData<List<Quiz>>

}