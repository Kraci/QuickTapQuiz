package com.kraci.quicktapquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.kraci.quicktapquiz.databinding.ActivityAddQuizCameraBinding

class AddQuizCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddQuizCameraBinding
    private lateinit var addQuizCameraViewModel: AddQuizCameraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Add Quiz"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_quiz_camera)

        addQuizCameraViewModel = ViewModelProviders.of(this).get(AddQuizCameraViewModel::class.java)

        binding.viewModel = addQuizCameraViewModel

        val jsonToParse = """
            {
	"name": "Python syntax",
	"categories": [
		{
			"name": "category1",
			"questions": [
				{
					"text": "question1text",
					"hint": "question1hint",
					"image": "base64encode",
					"value": "1000",
					"bonus": "false"
				},
				{
					"text": "question1text",
					"hint": "question1hint",
					"image": "base64encode",
					"value": "2000",
					"bonus": "false"
				},
				{
					"text": "question1text",
					"hint": "question1hint",
					"image": "base64encode",
					"value": "3000",
					"bonus": "false"
				},
				{
					"text": "question1text",
					"hint": "question1hint",
					"image": "base64encode",
					"value": "4000",
					"bonus": "false"
				},
				{
					"text": "question1text",
					"hint": "question1hint",
					"image": "base64encode",
					"value": "5000",
					"bonus": "true"
				}
			]
		},
		{
			"name": "category2",
			"questions": [
				{
					"text": "question2text",
					"hint": "question2hint",
					"image": "base64encode",
					"value": "100",
					"bonus": "false"
				},
				{
					"text": "question1text",
					"hint": "question1hint",
					"image": "base64encode",
					"value": "500",
					"bonus": "false"
				},
				{
					"text": "question1text",
					"hint": "question1hint",
					"image": "base64encode",
					"value": "1000",
					"bonus": "false"
				},
				{
					"text": "question1text",
					"hint": "question1hint",
					"image": "base64encode",
					"value": "2000",
					"bonus": "false"
				},
				{
					"text": "question1text",
					"hint": "question1hint",
					"image": "base64encode",
					"value": "3000",
					"bonus": "true"
				}
			]
		}
	]
}
        """.trimIndent()

        val jsonToParse2 = """
        {
	"status": true,
	"message": {
		"name": "Offline quiz",
		"categories": [
			{
				"name": "category1",
				"questions": [
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "",
						"value": "1000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "",
						"value": "2000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "3000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "4000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "5000",
						"bonus": "true"
					}
				]
			},
			{
				"name": "category2",
				"questions": [
					{
						"text": "question2text",
						"hint": "question2hint",
						"image": "base64encode",
						"value": "100",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "500",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "1000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "2000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "3000",
						"bonus": "true"
					}
				]
			},
			{
				"name": "category3",
				"questions": [
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "1000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "2000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "3000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "4000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "5000",
						"bonus": "true"
					}
				]
			},
			{
				"name": "category4",
				"questions": [
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "1000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "2000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "3000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "4000",
						"bonus": "false"
					},
					{
						"text": "question1text",
						"hint": "question1hint",
						"image": "base64encode",
						"value": "5000",
						"bonus": "true"
					}
				]
			}
		]
	}
}
        """.trimIndent()

//        addQuizCameraViewModel.saveParsedJSONtoDB(jsonToParse2)

    }
}
