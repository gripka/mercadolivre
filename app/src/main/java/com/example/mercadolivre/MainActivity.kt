package com.example.mercadolivre

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton: Button = findViewById(R.id.button4)
        val searchEditText: EditText = findViewById(R.id.editTextText)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("query", query)
            }
            startActivity(intent)
        }
    }
}