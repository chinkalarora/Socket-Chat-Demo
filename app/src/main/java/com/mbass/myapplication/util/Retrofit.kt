package com.mbass.myapplication.util

import android.content.Context
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.mbass.myapplication.BuildConfig

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Retrofit {

    companion object Singleton {
        private lateinit var mRetrofit: Retrofit
        private lateinit var mRetrofitStripe: Retrofit

        fun init(context: Context) {

            val httpClient = OkHttpClient.Builder()

            httpClient.readTimeout(10, TimeUnit.MINUTES)
            httpClient.connectTimeout(1, TimeUnit.MINUTES)
            httpClient.writeTimeout(10, TimeUnit.MINUTES)

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder()
                builder.header(
                    "authorization", "Bearer "+SharedPrefs.getToken(context)

                )
                builder.method(original.method(), original.body())
                chain.proceed(builder.build())
            }

            val interceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG)
                interceptor.level = HttpLoggingInterceptor.Level.BODY
            else
                interceptor.level = HttpLoggingInterceptor.Level.NONE

            httpClient.addInterceptor(interceptor)

            val client = httpClient.build()
            client.dispatcher().maxRequests = Integer.MAX_VALUE
            val gson = GsonBuilder()
                .setLenient()
                .create()


            mRetrofit = retrofit2.Retrofit.Builder()
                //  .baseUrl(if (SharedPrefs.getURL(MyApplication.getInstance()) != null) "${SharedPrefs.getURL(MyApplication.getInstance())}" else "https://stripe-issuing-europe.dev.jeev.es/v1/")
                .baseUrl(AppConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()


        }

        fun getRetrofit(): Retrofit {
            return mRetrofit
        }
    }
}