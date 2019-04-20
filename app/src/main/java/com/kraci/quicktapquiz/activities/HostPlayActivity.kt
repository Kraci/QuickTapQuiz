package com.kraci.quicktapquiz.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.R
import com.kraci.quicktapquiz.databinding.ActivityHostPlayBinding
import com.kraci.quicktapquiz.utils.IntentExtras
import com.kraci.quicktapquiz.viewmodels.GameAdapter
import com.kraci.quicktapquiz.viewmodels.HostPlayViewModel
import com.kraci.quicktapquiz.viewmodels.HostPlayViewModelFactory
import com.kraci.quicktapquiz.viewmodels.QuestionGame
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class HostPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostPlayBinding
    private lateinit var hostPlayViewModel: HostPlayViewModel
    private lateinit var gameAdapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameAdapter = intent.getParcelableExtra(IntentExtras.GAME_ADAPTER)
        val bonus = intent.getParcelableExtra<QuestionGame?>(IntentExtras.GAME_BONUS)

        supportActionBar?.title = "${gameAdapter.category} - ${gameAdapter.value}"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_play)

        hostPlayViewModel = ViewModelProviders.of(this,
            HostPlayViewModelFactory(application, gameAdapter, bonus)
        ).get(HostPlayViewModel::class.java).apply {

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

            bonusAlert.observe(this@HostPlayActivity, Observer {
                val dialog = AlertDialog.Builder(this@HostPlayActivity)
                dialog.setTitle("Bonus")
                dialog.setMessage("Bonus question")
                dialog.setNeutralButton("OK") { d, _ -> d.dismiss() }
                dialog.create()
                dialog.show()
            })

            imageToLoad.observe(this@HostPlayActivity, Observer {
                val imageView = findViewById<ImageView>(R.id.question_image)
                Picasso.get().load(it).networkPolicy(NetworkPolicy.OFFLINE).into(imageView)
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
            hostPlayViewModel.imageClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        hostPlayViewModel.disableVote()
        super.onBackPressed()
    }

}
