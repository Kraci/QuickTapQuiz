package com.kraci.quicktapquiz.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.R
import com.kraci.quicktapquiz.databinding.ActivityHostQuizPickerBinding
import com.kraci.quicktapquiz.viewmodels.HostQuizPickerViewModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizInfo(val id: Int, val name: String) : Parcelable

class HostQuizPickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostQuizPickerBinding
    private lateinit var hostQuizPickerViewModel: HostQuizPickerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Choose a Quiz"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_quiz_picker)

        hostQuizPickerViewModel = ViewModelProviders.of(this).get(HostQuizPickerViewModel::class.java).apply {

            quizItemClicked.observe(this@HostQuizPickerActivity, Observer {
                if (it.id != null) {
                    val quizInfo = QuizInfo(it.id, it.name)
                    val intent = Intent(this@HostQuizPickerActivity, HostTeamsWaitingActivity::class.java)
                    intent.putExtra("QuizInfo", quizInfo)
                    startActivity(intent)
                }
            })

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView.setHasFixedSize(true)
        binding.viewModel = hostQuizPickerViewModel
    }

}
