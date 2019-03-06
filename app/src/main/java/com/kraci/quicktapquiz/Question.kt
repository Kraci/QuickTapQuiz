package com.kraci.quicktapquiz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val text: String,
    val hint: String,
    val image: String)