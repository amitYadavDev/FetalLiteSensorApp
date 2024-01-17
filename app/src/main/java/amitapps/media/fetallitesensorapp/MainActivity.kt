package amitapps.media.fetallitesensorapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.VideoView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    var decodedResults: DoubleArray? = null
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Handle the broadcasted results here
             decodedResults = intent?.getDoubleArrayExtra("decoded_results")

            // Update UI with decoded results
            updateUI(decodedResults)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, DataProcessingService::class.java))




        // Start video playback in a VideoView or other component
        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.video
        videoView.setVideoPath(videoPath)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }

    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter("amitapps.media.fetallitesensorapp"))
    }

    private fun updateUI(decodedResults: DoubleArray?) {
        // Update your UI components (e.g., TextViews) with the decoded results
        if (decodedResults != null) {
            val textView1 = findViewById<TextView>(R.id.text1)
            val textView2 = findViewById<TextView>(R.id.text2)
            val textView3 = findViewById<TextView>(R.id.text3)
            val textView4 = findViewById<TextView>(R.id.text4)

            // Update your TextViews with the decoded results
            textView1.text = "Channel 1: ${decodedResults[0]}"
            textView2.text = "Channel 2: ${decodedResults[1]}"
            textView3.text = "Channel 3: ${decodedResults[2]}"
            textView4.text = "Channel 4: ${decodedResults[3]}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}