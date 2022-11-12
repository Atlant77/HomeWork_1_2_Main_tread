package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

private val logging = HttpLoggingInterceptor().apply{
    if (BuildConfig.DEBUG){
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface PostApiService {
    @GET("posts")
    fun getPosts(): Call<List<Post>>

    @POST("posts")
    fun save(@Body post: Post): Call<Post>

    @DELETE("posts/{id}")
    fun delete(@Path("id") postId: Long): Call<Unit>

    @POST("posts/{id}/like")
    fun like(@Path("id") postId: Long): Call<Post>

    @DELETE("posts/{id}/like")
    fun delLike(@Path("id") postId: Long): Call<Post>
}

object PostApiServiceHolder {
    val service: PostApiService by lazy { retrofit.create()}
}

