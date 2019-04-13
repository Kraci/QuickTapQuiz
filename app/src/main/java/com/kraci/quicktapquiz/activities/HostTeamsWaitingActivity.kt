package com.kraci.quicktapquiz.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.R
import com.kraci.quicktapquiz.databinding.ActivityHostTeamsWaitingBinding
import com.kraci.quicktapquiz.viewmodels.HostTeamsWaitingViewModel
import com.kraci.quicktapquiz.viewmodels.HostTeamsWaitingViewModelFactory

class HostTeamsWaitingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostTeamsWaitingBinding
    private lateinit var hostTeamsWaitingViewModel: HostTeamsWaitingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Joined Teams"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_teams_waiting)

        hostTeamsWaitingViewModel = ViewModelProviders.of(this,
            HostTeamsWaitingViewModelFactory(
                application,
                intent.getParcelableExtra<QuizInfo>("QuizInfo")
            )
        ).get(HostTeamsWaitingViewModel::class.java).apply {

            startQuizButton.observe(this@HostTeamsWaitingActivity, Observer {
                if (quizGame == null) {
                    Toast.makeText(baseContext, "Quiz is not valid.", Toast.LENGTH_LONG).show()
                } else {
                    val intent = Intent(this@HostTeamsWaitingActivity, HostPlayQuestionsActivity::class.java)
                    intent.putExtra("QuizGame", quizGame)
                    startActivity(intent)
                    finish()
                }
            })

        }

        binding.setLifecycleOwner(this)
        binding.hostedGames.layoutManager = LinearLayoutManager(baseContext)
        binding.hostedGames.setHasFixedSize(true)
        binding.viewModel = hostTeamsWaitingViewModel
    }

    override fun onBackPressed() {
        hostTeamsWaitingViewModel.stopAdvertise()
        hostTeamsWaitingViewModel.stopAllClients()
        super.onBackPressed()
    }

}
