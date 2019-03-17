package com.kraci.quicktapquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.kraci.quicktapquiz.databinding.ActivityHostTeamsWaitingBinding

class HostTeamsWaitingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostTeamsWaitingBinding
    private lateinit var hostTeamsWaitingViewModel: HostTeamsWaitingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Joined Teams"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_teams_waiting)

        hostTeamsWaitingViewModel = ViewModelProviders.of(this).get(HostTeamsWaitingViewModel::class.java).apply {

            startQuizButton.observe(this@HostTeamsWaitingActivity, Observer {
                println(".. START CREATING BUTTON TAPPED ..")
            })

        }

        binding.hostedGames.layoutManager = LinearLayoutManager(baseContext)
        binding.hostedGames.setHasFixedSize(true)
        binding.viewModel = hostTeamsWaitingViewModel
        binding.viewModel?.hostQuizName = intent.getStringExtra("QuizName")
        binding.viewModel?.startAdvertise()

        lifecycle.addObserver(hostTeamsWaitingViewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(hostTeamsWaitingViewModel)
    }

}
