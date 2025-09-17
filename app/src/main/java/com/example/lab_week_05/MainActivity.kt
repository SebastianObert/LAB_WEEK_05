package com.example.lab_week_05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.lab_week_05.api.CatApiService
import com.example.lab_week_05.model.ImageData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    private val apiResponseView: TextView by lazy {
        findViewById(R.id.api_response)
    }

    private val imageResultView: ImageView by lazy {
        findViewById(R.id.image_result)
    }

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")

        call.enqueue(object : Callback<List<ImageData>> {

            override fun onResponse(call: Call<List<ImageData>>, response: Response<List<ImageData>>) {
                if (response.isSuccessful) {
                    val imageList = response.body()
                    val imageUrl = imageList?.firstOrNull()?.imageUrl

                    if (!imageUrl.isNullOrBlank()) {
                        apiResponseView.text = getString(R.string.image_placeholder, imageUrl)

                        Glide.with(this@MainActivity)
                            .load(imageUrl)
                            .into(imageResultView)
                    } else {
                        Log.d(MAIN_ACTIVITY, "Image URL is null or empty")
                        apiResponseView.text = "Image URL not found"
                    }
                } else {
                    Log.e(MAIN_ACTIVITY, "Failed to get a successful response\n" +
                            response.errorBody()?.string().orEmpty())
                }
            }

            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
                apiResponseView.text = "Failed to fetch data. Check connection."
            }
        })
    }
}