package com.example.warehousev0

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.warehousev0.adapter.CategoryAdapter
import com.example.warehousev0.api.ApiClient
import com.example.warehousev0.model.Category
import com.example.warehousev0.ui.ProductListActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryListActivity : AppCompatActivity() {

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private var categoriesList: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        categoryRecyclerView = findViewById(R.id.recycler_view_categories)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)

        loadCategories()

        categoryRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(this, categoryRecyclerView, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: android.view.View, position: Int) {
                    val categoryId = categoriesList[position].id
                    val intent = Intent(this@CategoryListActivity, ProductListActivity::class.java)
                    intent.putExtra("CATEGORY_ID", categoryId)
                    startActivity(intent)
                }

                override fun onLongItemClick(view: android.view.View, position: Int) {
                }
            })
        )
    }

    private fun loadCategories() {
        val apiService = ApiClient.getApiService()
        apiService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    categoriesList = response.body() ?: emptyList()
                    categoryAdapter = CategoryAdapter(categoriesList)
                    categoryRecyclerView.adapter = categoryAdapter
                } else {
                    Toast.makeText(this@CategoryListActivity, "Не удалось загрузить категории", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@CategoryListActivity, "Ошибка загрузки категорий", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
