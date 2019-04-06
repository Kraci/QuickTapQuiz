package com.kraci.quicktapquiz

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "category_question",
        primaryKeys = ["category_id", "question_id"],
        foreignKeys = [
            ForeignKey(entity = Category::class,
                        parentColumns = ["id"],
                        childColumns = ["category_id"],
                        onDelete = CASCADE),
            ForeignKey(entity = Question::class,
                parentColumns = ["id"],
                childColumns = ["question_id"],
                onDelete = CASCADE)
        ])
data class CategoryQuestion(
    @ColumnInfo(name = "category_id")
    val categoryId: Int,
    @ColumnInfo(name = "question_id")
    val questionId: Int,
    @ColumnInfo(name = "question_value")
    val questionValue: Int,
    @ColumnInfo(name = "question_bonus")
    val questionBonus: Boolean)