package com.example.warehousev0.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.warehousev0.api.ApiClient
import com.example.warehousev0.databinding.ActivityEditProductBinding
import com.example.warehousev0.model.Category
import com.example.warehousev0.model.Product
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private var productId: Int = -1
    private var selectedCategoryId: Int = -1
    private var categories: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getIntExtra("PRODUCT_ID", -1)
        if (productId == -1) {
            Toast.makeText(this, "Товар не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadCategories()

        binding.buttonSave.setOnClickListener { updateProduct() }
    }

    private fun loadCategories() {
        ApiClient.getApiService().getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful && response.body() != null) {
                    categories = response.body()!!
                    val categoryNames = categories.map { it.name }
                    val adapter = ArrayAdapter(this@EditProductActivity, android.R.layout.simple_spinner_item, categoryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerCategory.adapter = adapter

                    loadProductData()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Ошибка загрузки категорий", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadProductData() {
        ApiClient.getApiService().getProductById(productId).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                val product = response.body()
                if (product != null) {
                    binding.editName.setText(product.name)
                    binding.editQuantity.setText(product.quantity.toString())

                    val index = categories.indexOfFirst { it.id == product.categoryId }
                    if (index >= 0) {
                        binding.spinnerCategory.setSelection(index)
                        selectedCategoryId = categories[index].id
                    }

                    binding.spinnerCategory.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                            selectedCategoryId = categories[position].id
                        }

                        override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
                    })
                } else {
                    Toast.makeText(this@EditProductActivity, "Ошибка загрузки товара", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Ошибка сети при загрузке товара", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProduct() {
        val name = binding.editName.text.toString().trim()
        val quantity = binding.editQuantity.text.toString().toIntOrNull()

        if (name.isEmpty() || quantity == null || selectedCategoryId == -1) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.getApiService().updateProduct(
            id = productId,
            name = name,
            quantity = quantity,
            categoryId = selectedCategoryId
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProductActivity, "Товар успешно обновлен", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProductActivity, "Ошибка при обновлении", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
