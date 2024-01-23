package amitapps.media.fetallitesensorapp

import amitapps.media.fetallitesensorapp.databinding.ActivityMainBinding
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.VideoView
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager

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
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        
        startService(Intent(this, DataProcessingService::class.java))

        // Start video playback in a VideoView
        val videoPath = "android.resource://" + packageName + "/" + R.raw.video
        binding.videoView.setVideoPath(videoPath)
        binding.videoView.setOnPreparedListener { mediaPlayer ->
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
        if (decodedResults != null) {
            binding.text1.text = "${decodedResults[0]}"
            binding.text2.text = "${decodedResults[1]}"
            binding.text3.text = "${decodedResults[2]}"
            binding.text4.text = "${decodedResults[3]}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}