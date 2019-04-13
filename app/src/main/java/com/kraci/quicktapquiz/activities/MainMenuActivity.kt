package com.kraci.quicktapquiz.activities

import android.Manifest
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kraci.quicktapquiz.R
import com.kraci.quicktapquiz.databinding.ActivityMainBinding
import com.kraci.quicktapquiz.viewmodels.MainMenuViewModel

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
	private lateinit var mainMenuViewModel: MainMenuViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

        supportActionBar?.title = ""

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainMenuViewModel = ViewModelProviders.of(this).get(MainMenuViewModel::class.java).apply {

            clickEvent.observe(this@MainMenuActivity, Observer {
                val intent = Intent(this@MainMenuActivity, it)
                startActivity(intent)
            })

        }

        binding.viewModel = mainMenuViewModel

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

}
