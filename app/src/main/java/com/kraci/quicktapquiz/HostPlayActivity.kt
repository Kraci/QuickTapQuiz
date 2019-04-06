package com.kraci.quicktapquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.databinding.ActivityHostPlayBinding

class HostPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostPlayBinding
    private lateinit var hostPlayViewModel: HostPlayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameAdapter = intent.getParcelableExtra<GameAdapter>("GameAdapter")

        supportActionBar?.title = gameAdapter.value.toString()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_play)

        hostPlayViewModel = ViewModelProviders.of(this, HostPlayViewModelFactory(application, gameAdapter)).get(HostPlayViewModel::class.java).apply {

            hintClicked.observe(this@HostPlayActivity, Observer {
                val dialog = AlertDialog.Builder(this@HostPlayActivity)
                dialog.setTitle("Hint")
                dialog.setMessage(it)
                dialog.setNeutralButton("OK") { d, _ -> d.dismiss() }
                dialog.create()
                dialog.show()
            })

            answerEvent.observe(this@HostPlayActivity, Observer {
                setResult(Activity.RESULT_OK)
                finish()
            })

        }

        binding.setLifecycleOwner(this)
        binding.votedTeams.layoutManager = LinearLayoutManager(baseContext)
        binding.votedTeams.setHasFixedSize(true)
        binding.viewModel = hostPlayViewModel
    }

    override fun onBackPressed() {
        hostPlayViewModel.disableVote()
        super.onBackPressed()
    }

}
