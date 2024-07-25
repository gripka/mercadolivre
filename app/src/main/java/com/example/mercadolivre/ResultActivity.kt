package com.example.mercadolivre

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class ResultActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val query = intent.getStringExtra("query") ?: ""
        resultTextView = findViewById(R.id.resultTextView)

        // Fazer a busca no Mercado Livre
        searchMercadoLivre(query)
    }

    private fun searchMercadoLivre(query: String) {
        val client = OkHttpClient()
        val url = "https://api.mercadolibre.com/sites/MLB/search?q=$query"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    resultTextView.text = "Erro ao buscar produtos: ${e.message}"
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let {
                    val responseData = it.string()
                    val json = JSONObject(responseData)
                    val results = json.getJSONArray("results")

                    val stringBuilder = StringBuilder()
                    for (i in 0 until results.length()) {
                        val product = results.getJSONObject(i)
                        val title = product.getString("title")
                        stringBuilder.append("$title\n")
                    }

                    runOnUiThread {
                        resultTextView.text = stringBuilder.toString()
                    }
                }
            }
        })
    }
}
