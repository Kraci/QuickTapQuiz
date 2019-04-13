package com.kraci.quicktapquiz.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kraci.quicktapquiz.database.Question

@Dao
interface QuestionDao {

    @Insert
    fun insert(question: Question): Long

    @Query("DELETE FROM questions")
    fun deleteAll()

}
