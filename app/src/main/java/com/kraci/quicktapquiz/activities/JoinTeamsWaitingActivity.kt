package com.kraci.quicktapquiz.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.R
import com.kraci.quicktapquiz.databinding.ActivityJoinTeamsWaitingBinding
import com.kraci.quicktapquiz.viewmodels.JoinTeamsWaitingViewModel
import com.kraci.quicktapquiz.viewmodels.JoinTeamsWaitingViewModelFactory

class JoinTeamsWaitingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinTeamsWaitingBinding
    private lateinit var joinTeamsWaitingViewModel: JoinTeamsWaitingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Joined Teams"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_teams_waiting)

        joinTeamsWaitingViewModel = ViewModelProviders.of(this,
            JoinTeamsWaitingViewModelFactory(
                application,
                intent.getParcelableExtra("QuizGame")
            )
        ).get(JoinTeamsWaitingViewModel::class.java).apply {

            startQuizEvent.observe(this@JoinTeamsWaitingActivity, Observer {
                val intent = Intent(this@JoinTeamsWaitingActivity, JoinPlayActivity::class.java)
                intent.putExtra("QuizGame", it)
                startActivity(intent)
                finish()
            })

            disconnectedFromHost.observe(this@JoinTeamsWaitingActivity, Observer {
                onBackPressed()
            })

        }

        binding.setLifecycleOwner(this)
        binding.joinedTeams.layoutManager = LinearLayoutManager(baseContext)
        binding.joinedTeams.setHasFixedSize(true)
        binding.viewModel = joinTeamsWaitingViewModel
    }

    override fun onBackPressed() {
        joinTeamsWaitingViewModel.disconnectFromHost()
        super.onBackPressed()
    }

}
