package com.kraci.quicktapquiz

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "categories", foreignKeys = [ForeignKey(entity = Quiz::class,
                                                            parentColumns = ["id"],
                                                            childColumns = ["quiz_id"])])
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    @ColumnInfo(name = "quiz_id")
    val quizId: Int)