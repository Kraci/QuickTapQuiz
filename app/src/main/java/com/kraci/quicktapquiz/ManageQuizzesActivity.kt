package com.kraci.quicktapquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.databinding.ActivityManageQuizzesBinding

class ManageQuizzesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageQuizzesBinding
    private lateinit var manageQuizzesViewModel: ManageQuizzesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Quizzes"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_quizzes)

        manageQuizzesViewModel = ViewModelProviders.of(this).get(ManageQuizzesViewModel::class.java).apply {

            addQuizEvent.observe(this@ManageQuizzesActivity, Observer {
                val intent = Intent(this@ManageQuizzesActivity, AddQuizCameraActivity::class.java)
                startActivity(intent)
            })

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView.setHasFixedSize(true)
        binding.viewModel = manageQuizzesViewModel
    }
}
