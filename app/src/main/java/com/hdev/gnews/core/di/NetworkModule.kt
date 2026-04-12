package com.hdev.gnews.core.di

import android.util.Log.d
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.hdev.gnews.BuildConfig
import com.hdev.gnews.core.utils.ConnectivityObserver
import com.hdev.gnews.core.utils.NetworkMonitor
import com.hdev.gnews.data.source.remote.ApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    
    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(connectivityObserver: ConnectivityObserver): NetworkMonitor
    
    companion object {
        
        @Provides
        @Singleton
        fun provideLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor { message ->
                val isJson = message.trim().startsWith("{") || message.trim().startsWith("[")
                if(isJson){
                    try {
                        //use Gson for reformat to pretty print
                        val jsonElement = JsonParser.parseString(message)
                        val prettyJson = GsonBuilder().setPrettyPrinting().create().toJson(jsonElement)
                        d("OkHttp", prettyJson)
                    } catch (e: Exception){
                        d("OkHttp", message)
                    }
                } else {
                    d("OkHttp", message)
                }
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val url = original.url.newBuilder()
                        .addQueryParameter("apiKey", BuildConfig.API_KEY)
                        .build()
                    chain.proceed(original.newBuilder().url(url).build())
                }
                .build()
        }
        
        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): ApiService {
            return retrofit.create(ApiService::class.java)
        }
    }
}
