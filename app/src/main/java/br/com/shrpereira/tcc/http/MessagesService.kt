package br.com.shrpereira.tcc.http

import br.com.shrpereira.tcc.model.Message
import retrofit2.Callback

class MessagesService {

    private val retrofit = RetrofitBuilder.retrofit.create(MessagesAPI::class.java)

    fun checkServer(callback: Callback<Message>) {
        retrofit.checkServer().enqueue(callback)
    }

    fun getNewMessage(callback: Callback<Message>) {
        retrofit.getNewMessage().enqueue(callback)
    }

    fun sendMessage(message: String, callback: Callback<Unit>) {
        retrofit.sendMessage(message).enqueue(callback)
    }
}