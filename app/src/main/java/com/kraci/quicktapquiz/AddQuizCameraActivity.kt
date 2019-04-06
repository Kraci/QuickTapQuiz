package com.kraci.quicktapquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AddQuizCameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_quiz_camera)

        supportActionBar?.title = "Add Quiz"

    }
}
