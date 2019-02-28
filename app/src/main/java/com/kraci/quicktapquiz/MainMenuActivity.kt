package com.kraci.quicktapquiz

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kraci.quicktapquiz.databinding.ActivityMainBinding

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
	private lateinit var mainMenuViewModel: MainMenuViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainMenuViewModel = ViewModelProviders.of(this).get(MainMenuViewModel::class.java).apply {
            this.activityEvent().observe(this@MainMenuActivity, Observer {
                val intent = Intent(this@MainMenuActivity, it)
                startActivity(intent)
            })
        }

        binding.viewModel = mainMenuViewModel
        lifecycle.addObserver(mainMenuViewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(mainMenuViewModel)
    }

}
