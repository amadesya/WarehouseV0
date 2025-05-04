package com.example.warehousev0.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.warehousev0.R
import com.example.warehousev0.api.ApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        val categoryNameInput = findViewById<EditText>(R.id.input_category_name)
        val addCategoryButton = findViewById<Button>(R.id.button_save_category)

        addCategoryButton.setOnClickListener {
            val categoryName = categoryNameInput.text.toString().trim()

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Заполните поле с названием категории", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.getApiService().addCategory(categoryName)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddCategoryActivity, "Категория добавлена", Toast.LENGTH_SHORT).show()

                            val resultIntent = Intent()
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        } else {
                            Toast.makeText(this@AddCategoryActivity, "Ошибка при добавлении категории", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@AddCategoryActivity, "Ошибка при добавлении категории", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
