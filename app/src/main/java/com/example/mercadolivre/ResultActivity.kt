package com.example.mercadolivre

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mercadolivre.databinding.ActivityResultBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var productAdapter: ProductAdapter
    private var currentPage = 1
    private var isLoading = false
    private var hasMoreItems = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val query = intent.getStringExtra("query") ?: ""
        productAdapter = ProductAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = productAdapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (hasMoreItems && !isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    // Trigger the load more data
                    loadMoreData(query)
                }
            }
        })

        if (query.isNotEmpty()) {
            loadMoreData(query)
        }
    }

    private fun loadMoreData(query: String) {
        isLoading = true
        val client = OkHttpClient()
        val url =
            "https://api.mercadolibre.com/sites/MLB/search?q=$query&offset=${(currentPage - 1) * 10}&limit=10"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Log.e("SearchError", "API request failed", e)
                    isLoading = false
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    response.body?.let {
                        val responseData = it.string()
                        Log.d("APIResponse", responseData)
                        try {
                            val json = JSONObject(responseData)
                            val results = json.optJSONArray("results") ?: org.json.JSONArray()

                            val products = mutableListOf<Product>()
                            for (i in 0 until results.length()) {
                                val product = results.getJSONObject(i)
                                val title = product.optString("title", "No title")
                                val price = product.optString("price", "No price")
                                val imageUrl = product.optString("thumbnail", "")
                                val productUrl = product.optString("permalink", "")
                                val productItem = Product(title, price, imageUrl, productUrl)
                                products.add(productItem)
                            }

                            runOnUiThread {
                                if (products.isNotEmpty()) {
                                    productAdapter.updateProducts(products)
                                    currentPage++
                                } else {
                                    hasMoreItems = false
                                }
                                isLoading = false
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Log.e("ParsingError", "Error parsing JSON", e)
                                isLoading = false
                            }
                        }
                    } ?: runOnUiThread {
                        Log.e("APIResponse", "Empty response body")
                        isLoading = false
                    }
                } else {
                    runOnUiThread {
                        Log.e("APIResponse", "API request failed with code ${response.code}")
                        isLoading = false
                    }
                }
            }
        })
    }
}