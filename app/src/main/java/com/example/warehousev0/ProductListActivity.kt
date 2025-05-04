package com.example.warehousev0.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warehousev0.R
import com.example.warehousev0.adapter.ProductAdapter
import com.example.warehousev0.api.ApiClient
import com.example.warehousev0.databinding.ActivityProductListBinding
import com.example.warehousev0.model.Category
import com.example.warehousev0.model.Product
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListActivity : AppCompatActivity(), ProductAdapter.OnProductClickListener {

    private lateinit var binding: ActivityProductListBinding
    private lateinit var adapter: ProductAdapter

    private lateinit var categories: List<Category>
    private lateinit var spinnerCategories: List<Category>

    private var allProducts: List<Product> = emptyList()
    private var selectedCategoryId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        loadCategories()

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }
        })

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategoryId = if (position == 0) null else spinnerCategories[position].id
                loadProductsByCategory(selectedCategoryId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                showAddDialog()
                true
            }
            R.id.action_refresh -> {
                loadProducts()
                Toast.makeText(this, "Список обновлен", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                prefs.edit().putBoolean("is_logged_in", false).apply()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddDialog() {
        val options = arrayOf("Добавить товар", "Добавить категорию")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Добавление")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> startActivity(Intent(this, AddProductActivity::class.java))
                1 -> startActivityForResult(Intent(this, AddCategoryActivity::class.java), REQUEST_ADD_CATEGORY)
            }
        }
        builder.show()
    }

    private fun loadCategories() {
        ApiClient.getApiService().getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                categories = response.body() ?: emptyList()

                val allCategories = mutableListOf(Category(0, "Все категории"))
                allCategories.addAll(categories)
                spinnerCategories = allCategories

                val categoryNames = spinnerCategories.map { it.name }

                val spinnerAdapter = ArrayAdapter(
                    this@ProductListActivity,
                    android.R.layout.simple_spinner_item,
                    categoryNames
                )
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.categorySpinner.adapter = spinnerAdapter

                binding.categorySpinner.onItemSelectedListener = null

                ApiClient.getApiService().getProducts().enqueue(object : Callback<List<Product>> {
                    override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                        allProducts = response.body() ?: emptyList()
                        adapter = ProductAdapter(allProducts, this@ProductListActivity)
                        binding.recyclerView.adapter = adapter

                        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                selectedCategoryId = if (position == 0) null else spinnerCategories[position].id
                                loadProductsByCategory(selectedCategoryId)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }

                        loadProductsByCategory(selectedCategoryId)
                    }

                    override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                        Toast.makeText(this@ProductListActivity, "Ошибка загрузки товаров", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@ProductListActivity, "Ошибка загрузки категорий", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun loadProducts() {
        ApiClient.getApiService().getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                allProducts = response.body() ?: emptyList()
                adapter = ProductAdapter(allProducts, this@ProductListActivity)
                binding.recyclerView.adapter = adapter
                loadProductsByCategory(selectedCategoryId)
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@ProductListActivity, "Ошибка загрузки товаров", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadProductsByCategory(categoryId: Int?) {
        if (categoryId != null) {
            ApiClient.getApiService().getProductsByCategory(categoryId).enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    val filteredProducts = response.body() ?: emptyList()
                    adapter.updateData(filteredProducts)
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    Toast.makeText(this@ProductListActivity, "Ошибка загрузки товаров для категории", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            adapter.updateData(allProducts)
        }
    }

    override fun onProductClick(product: Product) {
        showProductDialog(product)
    }

    private fun showProductDialog(product: Product) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(product.name)
        builder.setMessage("Количество: ${product.quantity}\nКатегория: ${getCategoryNameById(product.categoryId)}")

        builder.setPositiveButton("Редактировать") { _, _ ->
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            intent.putExtra("PRODUCT_NAME", product.name)
            intent.putExtra("PRODUCT_QUANTITY", product.quantity)
            intent.putExtra("PRODUCT_CATEGORY_ID", product.categoryId)
            startActivity(intent)
        }

        builder.setNegativeButton("Удалить") { _, _ -> deleteProduct(product.id) }

        builder.setNeutralButton("Отмена", null)

        builder.show()
    }

    private fun getCategoryNameById(categoryId: Int): String {
        return categories.find { it.id == categoryId }?.name ?: "Неизвестно"
    }

    private fun deleteProduct(productId: Int) {
        ApiClient.getApiService().deleteProduct(productId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(this@ProductListActivity, "Товар удалён", Toast.LENGTH_SHORT).show()
                loadProducts()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@ProductListActivity, "Ошибка при удалении", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_CATEGORY && resultCode == RESULT_OK) {
            loadCategories()
        }
    }

    companion object {
        const val REQUEST_ADD_CATEGORY = 1
    }
}
