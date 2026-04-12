package com.hdev.gnews.data.repository

import com.hdev.gnews.core.di.NetworkModule
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

class NewsRepositoryImplTest  {
    private lateinit var repository: NewsRepositoryImpl


    @get:Rule
    val name = TestName()

    @Before
    fun setup(){
        //using factory method exists in NetworkModule
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            //println("API_LOG: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = NetworkModule.provideOkHttpClient(loggingInterceptor)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient)
        val apiService = NetworkModule.provideApiService(retrofit)
        repository = NewsRepositoryImpl(apiService)
    }

    @Test
    fun `TEST FETCH EVERYTHING`() = runTest {
        //arrange: use sample query to fetch data from api
        val query = "xauusd"
        //act
        val result = repository.getEverything(query).toList()
        //Print
        ResponseTestHelper.printResult(name.methodName, result)
    }
}