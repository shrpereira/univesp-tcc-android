package br.com.shrpereira.tcc.websocket

import br.com.shrpereira.tcc.model.Message
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

interface MessagesWSService {

    @Receive
    fun observeWebSocketEvent(): Flowable<WebSocket.Event>

    @Receive
    fun observeMessages(): Flowable<Message>

    @Send
    fun sendMessage(message: String)
}