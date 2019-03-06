package com.kraci.quicktapquiz

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.launch

@Database(entities = arrayOf(Quiz::class), version = 1)
public abstract class QuizDatabase : RoomDatabase() {

    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): QuizDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                ).addCallback(QuizDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class QuizDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.quizDao())
                }
            }
        }

//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            INSTANCE?.let { database ->
//                scope.launch(Dispatchers.IO) {
//                    populateDatabase(database.quizDao())
//                }
//            }
//        }

        fun populateDatabase(quizDao: QuizDao) {
            quizDao.deleteAll()

            var quiz = Quiz(name = "USA Geographic")
            quizDao.insert(quiz)
            quiz = Quiz(name = "Elementary Math")
            quizDao.insert(quiz)
            quiz = Quiz(name = "Canada National Parks")
            quizDao.insert(quiz)
        }

    }

}