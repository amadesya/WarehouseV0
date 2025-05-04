package com.example.warehousev0.api

import com.example.warehousev0.model.Category
import com.example.warehousev0.model.LoginResponse
import com.example.warehousev0.model.Product
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("get_products.php")
    fun getProducts(): Call<List<Product>>

    @FormUrlEncoded
    @POST("add_product.php")
    fun addProduct(
        @Field("name") name: String,
        @Field("quantity") quantity: Int,
        @Field("category_id") categoryId: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("add_category.php")
    fun addCategory(
        @Field("name") name: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("update_quantity.php")
    fun updateQuantity(
        @Field("id") id: Int,
        @Field("quantity") quantity: Int
    ): Call<ResponseBody>

    @GET("get_categories.php")
    fun getCategories(): Call<List<Category>>

    @GET("get_product_by_id.php")
    fun getProductById(
        @Query("id") productId: Int
    ): Call<Product>

    @FormUrlEncoded
    @POST("delete_product.php")
    fun deleteProduct(
        @Field("id") productId: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("update_product.php")
    fun updateProduct(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("quantity") quantity: Int,
        @Field("category_id") categoryId: Int
    ): Call<ResponseBody>

    @GET("get_products_by_category.php")
    fun getProductsByCategory(@Query("category_id") categoryId: Int): Call<List<Product>>
}


