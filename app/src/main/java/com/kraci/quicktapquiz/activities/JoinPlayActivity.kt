package com.kraci.quicktapquiz.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kraci.quicktapquiz.R
import com.kraci.quicktapquiz.databinding.ActivityJoinPlayBinding
import com.kraci.quicktapquiz.viewmodels.HostedGame
import com.kraci.quicktapquiz.viewmodels.JoinPlayViewModel

class JoinPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinPlayBinding
    private lateinit var joinPlayViewModel: JoinPlayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val quizGame = intent.getParcelableExtra<HostedGame>("QuizGame")

        supportActionBar?.title = quizGame.teamName

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_play)

        joinPlayViewModel = ViewModelProviders.of(this).get(JoinPlayViewModel::class.java).apply {

            disconnectedFromHost.observe(this@JoinPlayActivity, Observer {
                finish()
            })

        }

        binding.setLifecycleOwner(this)
        binding.viewModel = joinPlayViewModel
    }

    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Do you really want to leave this game?")
        dialog.setPositiveButton("Yes") { _, _ ->
            joinPlayViewModel.disconnectFromHost()
            super.onBackPressed()
        }
        dialog.setNegativeButton("No") { d, _ -> d.dismiss() }
        dialog.create()
        dialog.show()
    }

}
