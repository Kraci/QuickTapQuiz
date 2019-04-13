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

@Database(
    entities = [
        Quiz::class,
        Category::class,
        CategoryQuestion::class,
        Question::class
    ],
    version = 2)
public abstract class QuizDatabase : RoomDatabase() {

    abstract fun quizDao(): QuizDao
    abstract fun categoryDao(): CategoryDao
    abstract fun questionDao(): QuestionDao
    abstract fun categoryQuestionDao(): CategoryQuestionDao
    abstract fun quizGameDao(): QuizGameDao

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
                ).fallbackToDestructiveMigration().addCallback(QuizDatabaseCallback(scope)).build()
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
                    populateDatabase(database.quizDao(), database.categoryDao(), database.questionDao(), database.categoryQuestionDao())
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

        fun populateDatabase(quizDao: QuizDao, categoryDao: CategoryDao, questionDao: QuestionDao, categoryQuestionDao: CategoryQuestionDao) {
            categoryQuestionDao.deleteAll()
            questionDao.deleteAll()
            categoryDao.deleteAll()
            quizDao.deleteAll()

            val questionText = "Je dávno známe, že ak je zrozumiteľný obsah stránky, na ktorej rozloženie sa čitateľ díva, jeho pozornosť je rozptýlená. Dôvodom použitia Lorem Ipsum je fakt, že má viacmenej normálne rozloženie písmen, takže oproti použitiu 'Sem príde text, sem príde text' sa obsah vypĺňanej oblasti na stránke viac podobá na skutočný text."

            var quiz = Quiz(name = "USA Geographic")
            var quizID = quizDao.insert(quiz)

            var category = Category(name = "Mountains", quizId = quizID.toInt())
            var categoryID = categoryDao.insert(category)

            var question = Question(text = questionText, hint = "hint1", image = "")
            var questionID = questionDao.insert(question)
            var categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 500, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = questionText, hint = "hint12", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 1000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = questionText, hint = "hint13", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 2000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = questionText, hint = "hint14", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 5000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = questionText, hint = "hint15", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 1500, true)
            categoryQuestionDao.insert(categoryQuestion)

            category = Category(name = "Rivers", quizId = quizID.toInt())
            categoryID = categoryDao.insert(category)

            question = Question(text = "River 1", hint = "hint21", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 500, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "River 2", hint = "hint22", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 1000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "River 3", hint = "hint23", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 2000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "River 4", hint = "hint24", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 5000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "River Bonus", hint = "hint25", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 2000, true)
            categoryQuestionDao.insert(categoryQuestion)

            category = Category(name = "Cities", quizId = quizID.toInt())
            categoryID = categoryDao.insert(category)

            question = Question(text = "City 1", hint = "hint31", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 500, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "City 2", hint = "hint32", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 1000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "City 3", hint = "hint33", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 2000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "City 4", hint = "hint34", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 5000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "City Bonus", hint = "hint35", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 500, true)
            categoryQuestionDao.insert(categoryQuestion)

            category = Category(name = "Buildings", quizId = quizID.toInt())
            categoryID = categoryDao.insert(category)

            question = Question(text = "Building 1", hint = "hint41", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 500, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "Building 2", hint = "hint42", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 1000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "Building 3", hint = "hint43", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 2000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "Building 4", hint = "hint44", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 5000, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "Buildings Bonus", hint = "hint45", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 5000, true)
            categoryQuestionDao.insert(categoryQuestion)

            quiz = Quiz(name = "Elementary Math")
            quizID = quizDao.insert(quiz)

            category = Category(name = "Category5", quizId = quizID.toInt())
            categoryID = categoryDao.insert(category)

            question = Question(text = "Question51", hint = "hint51", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 300, false)
            categoryQuestionDao.insert(categoryQuestion)

            question = Question(text = "Question52", hint = "hint52", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 600, false)
            categoryQuestionDao.insert(categoryQuestion)

            category = Category(name = "Category6", quizId = quizID.toInt())
            categoryID = categoryDao.insert(category)

            question = Question(text = "Question61", hint = "hint61", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 1, false)
            categoryQuestionDao.insert(categoryQuestion)

            quiz = Quiz(name = "Canada National Parks")
            quizID = quizDao.insert(quiz)

            category = Category(name = "Category7", quizId = quizID.toInt())
            categoryID = categoryDao.insert(category)

            question = Question(text = "Question71", hint = "hint71", image = "")
            questionID = questionDao.insert(question)
            categoryQuestion = CategoryQuestion(categoryID.toInt(), questionID.toInt(), 1, false)
            categoryQuestionDao.insert(categoryQuestion)
        }

    }

}