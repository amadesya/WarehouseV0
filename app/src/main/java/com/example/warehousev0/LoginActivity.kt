package com.example.warehousev0.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.warehousev0.R
import com.example.warehousev0.api.ApiClient
import com.example.warehousev0.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        if (prefs.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, ProductListActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val usernameInput = findViewById<EditText>(R.id.username_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginBtn = findViewById<Button>(R.id.login_button)
        val rememberMe = findViewById<CheckBox>(R.id.remember_me_checkbox)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            ApiClient.getApiService().login(username, password)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.body()?.success == true) {
                            if (rememberMe.isChecked) {
                                prefs.edit().putBoolean("is_logged_in", true).apply()
                            }
                            startActivity(Intent(this@LoginActivity, ProductListActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        t.printStackTrace()
                        Log.e("LOGIN_ERROR", "Ошибка подключения: ${t.localizedMessage}")
                        Toast.makeText(this@LoginActivity, "Сервер недоступен", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
