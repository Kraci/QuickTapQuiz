package com.kraci.quicktapquiz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_table")
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String)