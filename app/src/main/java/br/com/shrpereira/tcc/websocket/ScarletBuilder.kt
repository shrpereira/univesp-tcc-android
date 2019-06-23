package br.com.shrpereira.tcc.websocket

import android.app.Application
import br.com.shrpereira.tcc.BuildConfig
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

private const val WS_URL_PROD = "wss://univesp-tcc-backend.herokuapp.com/messages"
private const val WS_URL_DEV = "ws://192.168.0.18:8080/messages"

class ScarletBuilder {

    companion object {

        private val webSocketUrl: String
            get() = if (BuildConfig.DEBUG) WS_URL_DEV else WS_URL_PROD

//        private val lifecycle = AndroidLifecycle.ofApplicationForeground(Application())
        private val backoffStrategy = ExponentialWithJitterBackoffStrategy(5000, 5000)

        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        private val scarletInstance: Scarlet
            get() = Scarlet.Builder()
                .webSocketFactory(okHttpClient.newWebSocketFactory(webSocketUrl))
                .addMessageAdapterFactory(GsonMessageAdapter.Factory())
                .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
                .backoffStrategy(backoffStrategy)
//                .lifecycle(lifecycle)
                .build()

        val messagesService
            get() = scarletInstance.create<MessagesWSService>()

    }
}