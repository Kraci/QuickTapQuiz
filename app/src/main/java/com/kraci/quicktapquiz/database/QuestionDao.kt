package com.kraci.quicktapquiz.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuestionDao {

    @Insert
    fun insert(question: Question): Long

    @Query("DELETE FROM questions")
    fun deleteAll()

}
