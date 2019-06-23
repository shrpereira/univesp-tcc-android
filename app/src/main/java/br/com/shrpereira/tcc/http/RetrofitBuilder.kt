package br.com.shrpereira.tcc.http

import br.com.shrpereira.tcc.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


private const val API_URL_PROD = "https://univesp-tcc-backend.herokuapp.com"
private const val API_URL_DEV = "http://192.168.0.18:8080/"

class RetrofitBuilder {

    companion object {

        private val apiUrl: String
            get() = if (BuildConfig.DEBUG) API_URL_DEV else API_URL_PROD

        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build()
    }
}