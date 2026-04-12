package com.hdev.gnews.data.repository

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.hdev.gnews.domain.model.Resource


class ResponseTestHelper {
    companion object {
        private val gson = GsonBuilder().setPrettyPrinting().create()
        fun printResult(testName: String, results: List<Resource<*>>){
            println("\n=== $testName ===")
            results.forEachIndexed { index, resource ->
                val prefix = "Step $index:"
                when(resource){
                    is Resource.Loading -> println("$prefix [LOADING...]")
                    is Resource.Success -> {
                        println("$prefix [SUCCESS]")
                        println("   Body: ${gson.toJson(resource.data)}")
                    }
                    is Resource.Error -> {
                        println("$prefix [ERROR]")
                        //pretty print error body
                        val cleanMessage = formatErrorMessage(resource.message)
                        println("Message: $cleanMessage")
                    }
                }
            }
            println("\n===========================\n")
        }

        private fun formatErrorMessage(message: String?) : String {
            if(message.isNullOrBlank()) return "Unknown Error"
            return try {

                val jsonObject = gson.fromJson(message, JsonObject::class.java)
                if (jsonObject.has("error")) {
                    jsonObject.get("error").asString
                } else {
                    message
                }
            } catch (e: Exception) {

                message
            }
        }
    }
}
