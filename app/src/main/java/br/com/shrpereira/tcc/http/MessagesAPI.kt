package br.com.shrpereira.tcc.http

import br.com.shrpereira.tcc.model.Message
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MessagesAPI {

    @GET("/message")
    fun getNewMessage(): Call<Message>

    @GET("/health")
    fun checkServer(): Call<Message>

    @POST("/message")
    fun sendMessage(@Query("message") message: String): Call<Unit>
}
