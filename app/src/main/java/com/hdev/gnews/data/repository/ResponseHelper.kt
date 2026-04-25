package com.hdev.gnews.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hdev.gnews.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

open class ResponseHelper {
    private val gson = Gson()

    protected fun <T> saveApiCall(apiCall: suspend () -> Response<T>) : Flow<Resource<T>> = flow {
        try {
            val response = apiCall()
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                }    ?: emit(Resource.Error("Empty response body"))

            } else {
                //Read error body once to avoid stream
                val errorBodyString = response.errorBody()?.string()
                val errorMessage = parseErrorMessage(errorBodyString, response.code())
                emit(Resource.Error(errorMessage))

            }
        } catch (e: Exception){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    protected fun parseErrorMessage(errorJsonString: String?, responseCode: Int) : String {
        if(errorJsonString.isNullOrBlank()) return "Error :$responseCode"
        return try {
            val jsonObject = gson.fromJson(errorJsonString, JsonObject::class.java)
            if(jsonObject.has("error")){
                jsonObject.get("error").asString
            } else {
                errorJsonString
            }
        } catch (e: Exception){
            errorJsonString
        }
    }
}
