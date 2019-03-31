package com.kraci.quicktapquiz

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
                val intent = Intent(this@HostPlayQuestionsActivity, HostPlayActivity::class.java)
                intent.putExtra("GameAdapter", it)
                startActivityForResult(intent, 0)
            })

        }

        binding.setLifecycleOwner(this)
        binding.hostedQuizQuestions.layoutManager = LinearLayoutManager(baseContext)
        binding.hostedQuizQuestions.setHasFixedSize(true)
        binding.viewModel = hostPlayQuestionsViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.score_button, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null && item.itemId == R.id.score) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Score")
            dialog.setItems(hostPlayQuestionsViewModel.teamScores()) { _, _ -> }
            dialog.setNeutralButton("OK") { d, _ -> d.dismiss() }
            dialog.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

}
