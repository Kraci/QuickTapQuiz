package com.kraci.quicktapquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.databinding.ActivityHostPlayQuestionsBinding

class HostPlayQuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostPlayQuestionsBinding
    private lateinit var hostPlayQuestionsViewModel: HostPlayQuestionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val quizGame = intent.getParcelableExtra<QuizGame>("QuizGame")

        supportActionBar?.title = quizGame.name

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_play_questions)

        hostPlayQuestionsViewModel = ViewModelProviders.of(this, HostPlayQuestionsViewModelFactory(application, quizGame)).get(HostPlayQuestionsViewModel::class.java).apply {
            questionChoosed.observe(this@HostPlayQuestionsActivity, Observer {
                // present new activity
            })
        }

        binding.setLifecycleOwner(this)
        binding.hostedQuizQuestions.layoutManager = LinearLayoutManager(baseContext)
        binding.hostedQuizQuestions.setHasFixedSize(true)
        binding.viewModel = hostPlayQuestionsViewModel
    }
}
