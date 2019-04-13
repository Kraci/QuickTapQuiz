package com.kraci.quicktapquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.databinding.ActivityHostPlayBinding

class HostPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostPlayBinding
    private lateinit var hostPlayViewModel: HostPlayViewModel
    private lateinit var gameAdapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameAdapter = intent.getParcelableExtra("GameAdapter")
        val bonus = intent.getParcelableExtra<QuestionGame?>("GameAdapterBonus")

        supportActionBar?.title = "${gameAdapter.category} - ${gameAdapter.value}"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_play)

        hostPlayViewModel = ViewModelProviders.of(this, HostPlayViewModelFactory(application, gameAdapter, bonus)).get(HostPlayViewModel::class.java).apply {

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!gameAdapter.image.isEmpty()) {
            menuInflater.inflate(R.menu.image_button, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null && item.itemId == R.id.quiz_image) {
            // handle image
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        hostPlayViewModel.disableVote()
        super.onBackPressed()
    }

}
