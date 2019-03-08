package com.kraci.quicktapquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.databinding.ActivityHostQuizPickerBinding

class HostQuizPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostQuizPickerBinding
    private lateinit var hostQuizPickerViewModel: HostQuizPickerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Choose a Quiz"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_quiz_picker)

        hostQuizPickerViewModel = ViewModelProviders.of(this).get(HostQuizPickerViewModel::class.java).apply {
            this.quizItemObservable().observe(this@HostQuizPickerActivity, Observer {
                val intent = Intent(this@HostQuizPickerActivity, HostTeamsWaitingActivity::class.java)
                intent.putExtra("QuizName", it.name)
                startActivity(intent)
            })
            this.allQuizzes.observe(this@HostQuizPickerActivity, Observer {
                adapter.quizzes = it
            })
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView.setHasFixedSize(true)
        // preto toto nedat sem? aka je vyhoda ze to je v xml a preco to je vlastne vo view modely
        // binding.recyclerView.adapter = hostQuizPickerViewModel.adapter
        binding.viewModel = hostQuizPickerViewModel

        lifecycle.addObserver(hostQuizPickerViewModel)

    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(hostQuizPickerViewModel)
    }

}
