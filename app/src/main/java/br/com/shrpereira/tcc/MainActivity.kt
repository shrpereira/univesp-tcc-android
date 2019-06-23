package br.com.shrpereira.tcc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import br.com.shrpereira.tcc.databinding.ActivityMainBinding
import br.com.shrpereira.tcc.http.MessagesService
import br.com.shrpereira.tcc.model.Message
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val messagesService by lazy { MessagesService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        configureListeners()
    }

    private fun configureListeners() {
        binding.button0.setOnClickListener {
            messagesService.checkServer(object : Callback<Message> {
                override fun onResponse(call: Call<Message>, response: Response<Message>) {
                    Toast
                        .makeText(this@MainActivity, response.body()?.message, Toast.LENGTH_LONG)
                        .show()
                }

                override fun onFailure(call: Call<Message>, t: Throwable) {
                    Toast
                        .makeText(
                            this@MainActivity,
                            "Failed to check server health",
                            Toast.LENGTH_LONG
                        )
                        .show()
                }

            })
        }
        binding.button1.setOnClickListener {
            startActivity(Intent(this@MainActivity, PollingHttpActivity::class.java))
        }
        binding.button2.setOnClickListener {
            startActivity(Intent(this@MainActivity, WebSocketActivity::class.java))
        }
    }
}
