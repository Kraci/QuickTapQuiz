package com.kraci.quicktapquiz.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kraci.quicktapquiz.R
import com.kraci.quicktapquiz.databinding.ActivityManageQuizzesBinding
import com.kraci.quicktapquiz.viewmodels.ManageQuizzesViewModel

class ManageQuizzesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageQuizzesBinding
    private lateinit var manageQuizzesViewModel: ManageQuizzesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Quizzes"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_quizzes)

        manageQuizzesViewModel = ViewModelProviders.of(this).get(ManageQuizzesViewModel::class.java).apply {

            addQuizEvent.observe(this@ManageQuizzesActivity, Observer {
                val dialog = AlertDialog.Builder(this@ManageQuizzesActivity)
                dialog.setTitle("Quiz code")
                val input = EditText(this@ManageQuizzesActivity)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                dialog.setView(input)
                dialog.setPositiveButton("OK") { d, _ ->
                    val code = input.text.toString().toInt()
                    if (code == 0) {
                        Toast.makeText(baseContext, "Not valid code.", Toast.LENGTH_LONG).show()
                    } else {
                        request(code)
                    }
                    d.dismiss()
                }
                dialog.setNegativeButton("Cancel") { d, _ -> d.dismiss()}
                dialog.create()
                dialog.show()
            })

            notValidCodeEvent.observe(this@ManageQuizzesActivity, Observer {
                Toast.makeText(baseContext, "Not valid code.", Toast.LENGTH_LONG).show()
            })

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView.setHasFixedSize(true)
        binding.viewModel = manageQuizzesViewModel
    }
}
