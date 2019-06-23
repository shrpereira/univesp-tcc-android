package br.com.shrpereira.tcc

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import br.com.shrpereira.tcc.databinding.ActivityPollingHttpBinding
import br.com.shrpereira.tcc.http.MessagesService
import br.com.shrpereira.tcc.model.Message
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


class PollingHttpActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    private lateinit var binding: ActivityPollingHttpBinding
    private lateinit var messagesJob: Job
    private lateinit var senderJob: Job

    private val messagesService by lazy { MessagesService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_polling_http)

        configureTests()
    }

    override fun onPause() {
        super.onPause()

        if (::messagesJob.isInitialized && messagesJob.isActive) messagesJob.cancel()
        if (::senderJob.isInitialized && senderJob.isActive) senderJob.cancel()
    }

    private fun configureTests() {
        configureTest(binding.oneSecButton, binding.oneSecondResult, 1000L, 180)
        configureTest(binding.fiveSecButton, binding.fiveSecondsResult, 5000L, 36)
        configureTest(binding.tenSecButton, binding.tenSecondsResult, 10000L, 18)
        configureTest(binding.thirtySecButton, binding.thirtySecondsResult, 30000L, 6)
    }

    private fun configureTest(
        button: MaterialButton,
        resultTextView: TextView,
        interval: Long,
        iterations: Int
    ) {
        button.setOnClickListener {
            messagesJob = launch {
                withContext(Dispatchers.IO) {
                    withContext(Dispatchers.Main) {
                        button.isEnabled = false
                    }

                    startPollingTest(
                        interval,
                        resultTextView,
                        iterations
                    )

                    withContext(Dispatchers.Main) {
                        senderJob.cancel()
                        button.isEnabled = true
                    }
                }
            }.also { it.start() }

            senderJob = launch {
                withContext(Dispatchers.IO) {
                    sendMessages(getString(R.string.this_is_a_text_message))
                }
            }.also { it.start() }
        }
    }

    private suspend fun startPollingTest(
        interval: Long,
        resultView: TextView,
        iterationCount: Int
    ) {
        for (i in 1..iterationCount) {
            delay(interval)
            checkServerForMessages(i)
            withContext(Dispatchers.Main) {
                binding.testCount.text = "$i"
            }
        }
        withContext(Dispatchers.Main) {
            resultView.text = getString(R.string.x_seconds_test_finished, interval / 1000)
        }
    }

    private fun checkServerForMessages(
        count: Int
    ) {
        messagesService.getNewMessage(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                Log.d(
                    "PollingHttpActivity", response.body()?.message ?: " - Count: $count"
                )
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                Log.d("PollingHttpActivity", t.message ?: " - Count: $count")
            }
        })
    }

    private suspend fun sendMessages(message: String) {
        for (i in 1..36) {
            delay(5000L)
            messagesService.sendMessage(message, object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    Log.d(
                        "PollingHttpActivity", "Message Sent"
                    )
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("PollingHttpActivity", t.message)
                }
            })
        }
    }
}
