package com.example.warehousev0.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val name: String,
    val quantity: Int,
    @SerializedName("category_id")
    val categoryId: Int
)

