package com.kraci.quicktapquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class HostTeamsWaiting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Joined Teams"

        setContentView(R.layout.activity_host_teams_waiting)

        val view = findViewById<TextView>(R.id.shittyButton)
        view.text = intent.getStringExtra("QuizName")

    }
}
