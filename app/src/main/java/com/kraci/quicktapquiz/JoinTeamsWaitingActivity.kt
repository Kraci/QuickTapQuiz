package com.kraci.quicktapquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.databinding.ActivityJoinTeamsWaitingBinding

class JoinTeamsWaitingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinTeamsWaitingBinding
    private lateinit var joinTeamsWaitingViewModel: JoinTeamsWaitingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Joined Teams"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_teams_waiting)

        joinTeamsWaitingViewModel = ViewModelProviders.of(this, JoinTeamsWaitingViewModelFactory(application, intent.getParcelableExtra("QuizGame"))).get(JoinTeamsWaitingViewModel::class.java)

        binding.setLifecycleOwner(this)
        binding.joinedTeams.layoutManager = LinearLayoutManager(baseContext)
        binding.joinedTeams.setHasFixedSize(true)
        binding.viewModel = joinTeamsWaitingViewModel
    }

}
