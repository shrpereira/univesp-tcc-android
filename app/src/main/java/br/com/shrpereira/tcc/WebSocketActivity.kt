package br.com.shrpereira.tcc

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import br.com.shrpereira.tcc.databinding.ActivityWebSocketBinding
import br.com.shrpereira.tcc.websocket.ScarletBuilder
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class WebSocketActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    private lateinit var binding: ActivityWebSocketBinding
    private val messageService by lazy { ScarletBuilder.messagesService }

    private lateinit var messagesJob: Job
    private lateinit var senderJob: Job
    private lateinit var tickerJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_socket)

        configureTests()
    }

    override fun onPause() {
        super.onPause()

        finishJobs()
    }

    private fun configureTests() {
        binding.oneSecButton.setOnClickListener { configureTest(binding.oneSecondResult, "first") }
        binding.fiveSecButton.setOnClickListener {
            configureTest(
                binding.fiveSecondsResult,
                "second"
            )
        }
        binding.tenSecButton.setOnClickListener { configureTest(binding.tenSecondsResult, "third") }
        binding.thirtySecButton.setOnClickListener {
            configureTest(
                binding.thirtySecondsResult,
                "forth"
            )
        }
    }

    private fun configureTest(result: TextView, initMessage: String) {
        tickerJob = launch(Dispatchers.Main) {
            var count = 0
            while (true) {
                count++
                binding.testCount.text = "$count"
                delay(1000L)
            }
        }

        messagesJob = launch {
            withContext(Dispatchers.IO) {
                messageService.observeWebSocketEvent()
                    .filter { it is WebSocket.Event.OnConnectionOpened<*> }
                    .subscribe {
                        Log.d("WebSocketActivity", "Connection Opened")
                    }
            }

            messageService.observeMessages()
                .subscribe { message ->
                    Log.d("WebSocketActivity", "Message: ${message.message}")
                }

            withContext(Dispatchers.Main) {
                delay(180000)
                finishJobs()
                result.text = getString(R.string.messages_test_finished)
            }
        }

        senderJob = launch {
            withContext(Dispatchers.IO) {
                messageService.sendMessage(initMessage)

                for (i in 1..36) {
                    delay(5000L)
                    messageService.sendMessage("This is a message")
                }
            }
        }
    }

    private fun finishJobs() {
        messageService.sendMessage("bye")
        if (::messagesJob.isInitialized && messagesJob.isActive) messagesJob.cancel()
        if (::senderJob.isInitialized && senderJob.isActive) senderJob.cancel()
        if (::tickerJob.isInitialized && tickerJob.isActive) tickerJob.cancel()
    }
}
