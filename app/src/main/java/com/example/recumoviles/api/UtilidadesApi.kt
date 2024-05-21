package com.example.recumoviles.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UtilidadesApi {
    companion object {
        const val BASE_URL = "https://api.unsplash.com/"
        const val API_KEY = "81fzT4gW4BJmVNqLhT7P-MnB9wVHOVs8_KzHt2vceZE"
        private var retrofit: Retrofit? = null

        fun getApiInterface(): ApiInterface {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!.create(ApiInterface::class.java)
        }
    }
}
