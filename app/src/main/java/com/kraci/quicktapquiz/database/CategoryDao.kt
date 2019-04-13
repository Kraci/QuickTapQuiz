package com.kraci.quicktapquiz.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kraci.quicktapquiz.database.Category

@Dao
interface CategoryDao {

    @Insert
    fun insert(category: Category): Long

    @Query("DELETE FROM categories")
    fun deleteAll()

}