package com.example.warehousev0.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.warehousev0.R
import com.example.warehousev0.api.ApiClient
import com.example.warehousev0.model.Category
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddProductActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private var categoryMap: Map<String, Int> = mapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        val nameInput = findViewById<EditText>(R.id.input_product_name)
        val quantityInput = findViewById<EditText>(R.id.input_product_quantity)
        val addButton = findViewById<Button>(R.id.button_save_product)
        categorySpinner = findViewById(R.id.spinner_category)

        loadCategoriesFromServer()

        addButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val quantityText = quantityInput.text.toString().trim()

            if (name.isEmpty() || quantityText.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = quantityText.toIntOrNull()
            if (quantity == null || quantity < 0) {
                Toast.makeText(this, "Введите корректное количество", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCategoryName = categorySpinner.selectedItem.toString()
            val categoryId = categoryMap[selectedCategoryName] ?: 1

            ApiClient.getApiService().addProduct(name, quantity, categoryId)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        Toast.makeText(this@AddProductActivity, "Товар добавлен", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@AddProductActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun loadCategoriesFromServer() {
        ApiClient.getApiService().getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                val categories = response.body() ?: listOf()
                categoryMap = categories.associate { it.name to it.id }

                val categoryNames = categories.map { it.name }
                val adapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_spinner_item, categoryNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = adapter
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@AddProductActivity, "Не удалось загрузить категории", Toast.LENGTH_SHORT).show()
            }
        })
    }
}