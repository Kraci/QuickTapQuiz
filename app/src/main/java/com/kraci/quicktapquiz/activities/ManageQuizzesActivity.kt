package com.kraci.quicktapquiz.activities

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
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
import com.kraci.quicktapquiz.utils.APIEndpoints
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
                if (!availableInternet()) {
                    Toast.makeText(baseContext, "Internet connection is needed.", Toast.LENGTH_LONG).show()
                    return@Observer
                }
                val dialog = AlertDialog.Builder(this@ManageQuizzesActivity)
                dialog.setTitle("Quiz code")
                dialog.setMessage("Create quiz at ${APIEndpoints.SERVER_URL} and obtain code.")
                val input = EditText(this@ManageQuizzesActivity)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                dialog.setView(input)
                dialog.setPositiveButton("OK") { d, _ ->
                    val code = input.text.toString().toInt()
                    request(code)
                    d.dismiss()
                }
                dialog.setNegativeButton("Cancel") { d, _ -> d.dismiss()}
                dialog.create()
                dialog.show()
            })

            notValidCodeEvent.observe(this@ManageQuizzesActivity, Observer {
                Toast.makeText(baseContext, it, Toast.LENGTH_LONG).show()
            })

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView.setHasFixedSize(true)
        binding.viewModel = manageQuizzesViewModel
    }

    fun availableInternet(): Boolean {
        val connectivityManager = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}
