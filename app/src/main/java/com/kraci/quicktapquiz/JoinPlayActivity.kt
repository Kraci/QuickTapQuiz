package com.kraci.quicktapquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.kraci.quicktapquiz.databinding.ActivityJoinPlayBinding

class JoinPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinPlayBinding
    private lateinit var joinPlayViewModel: JoinPlayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val quizGame = intent.getParcelableExtra<Game>("QuizGame")

        supportActionBar?.title = quizGame.teamName

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_play)

        joinPlayViewModel = ViewModelProviders.of(this, JoinPlayViewModelFactory(application, quizGame)).get(JoinPlayViewModel::class.java)

        binding.setLifecycleOwner(this)
        binding.viewModel = joinPlayViewModel
    }

}
