package amitapps.media.fetallitesensorapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern

class DataProcessingService : Service() {

    private lateinit var executor: ExecutorService

    override fun onCreate() {
        super.onCreate()
        executor = Executors.newFixedThreadPool(4)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MainActivityabcf", "onStartCommand")
        processFileInBackground()
        return START_STICKY
    }

    private fun processFileInBackground() {
        CoroutineScope(Dispatchers.IO).launch {
            // Read data from file
            val data = readDataFromFile()

            // Process and update UI
            processAndDisplayData(data)
        }
    }

    private fun readDataFromFile(): List<List<Double>> {
        // Implement logic to read data from the file
        // Return a list of channels, each containing a list of sample values
        // Example: List<List<Double>> = [[channel1], [channel2], [channel3], [channel4]]
        // Declaring an input stream to read data

//
//        val data = """"""
//        val delim = "!"

        val wordsList = mutableListOf<String>()
        try {
            val inputStream = resources.openRawResource(R.raw.input)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                var words = line?.split("\\s+".toRegex()) // Split the line into words
                if (words != null) {
                    words = words.reversed()
//                    Log.d("converted_data_size", words.toString() + "   " + words.size.toString())

                    wordsList.addAll(words)
                }
            }

            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val channelData = wordsList.map { sample ->
            Regex("[0-9A-F]{5}").findAll(sample).map { it.value }.toList()
        }

        // Extract samples and channel data
//        val samples = Regex("""!(.*?)!""").findAll(data).map { it.groupValues[1] }.toList()
//        val channelData = list.map { sample ->
//            Regex("[0-9A-F]{4}").findAll(sample).map { it.value }.toList()
//        }
//
//        // Convert hex to double for each channel
//        val convertedData = channelData.map { sample ->
//            sample.map { hexValue ->
//                Integer.parseInt(hexValue, 16).toDouble() / 1000.0 // Assuming voltage is in millivolts
//            }
//        }

        // Print the converted data
        channelData.forEach { sample ->
                Log.d("converted_data_", sample.toString())
        }


        val listOfLists: List<List<Double>> = listOf(
            listOf(1.0, 2.0, 3.0, 4.0),
            listOf(4.0, 5.0, 6.0, 4.0),
            listOf(7.0, 8.0, 9.0, 4.0),
            listOf(1.0, 2.0, 3.0, 4.0),
            listOf(4.0, 5.0, 6.0, 4.0),
            listOf(7.0, 8.0, 9.0, 4.0),
            listOf(1.0, 2.0, 3.0, 4.0),
            listOf(4.0, 5.0, 6.0, 4.0),
            listOf(7.0, 8.0, 9.0, 4.0),
            listOf(1.0, 2.0, 3.0, 4.0),
            listOf(4.0, 5.0, 6.0, 4.0),
            listOf(7.0, 8.0, 9.0, 4.0),
            listOf(1.0, 2.0, 3.0, 4.0),
            listOf(4.0, 5.0, 6.0, 4.0),
            listOf(7.0, 8.0, 9.0, 4.0),
            listOf(1.0, 2.0, 3.0, 4.0),
            listOf(4.0, 5.0, 6.0, 4.0),
            listOf(7.0, 8.0, 9.0, 4.0),
            listOf(1.0, 2.0, 3.0, 4.0),
            listOf(4.0, 5.0, 6.0, 4.0),
            listOf(7.0, 8.0, 9.0, 4.0),
            listOf(1.0, 2.0, 3.0, 4.0),
            listOf(4.0, 5.0, 6.0, 4.0),
            listOf(7.0, 8.0, 9.0, 4.0)
        )


        return listOfLists
    }

    private suspend fun processAndDisplayData(data: List<List<Double>>) = withContext(Dispatchers.Main) {
        val startTime = System.currentTimeMillis()

        for (i in data.indices) {
            val elapsedTime = System.currentTimeMillis() - startTime
            Log.d("MainActivityabcindices", i.toString())

            // Process each channel in parallel
            val deferredList = ArrayList<Deferred<Double>>()
            for (channelIndex in 0 until 4) {
                val deferred = async(executor.asCoroutineDispatcher()) {
                    // Decode channel data[channelIndex][i]
                    // Example: Your decoding logic
                    data[i][channelIndex]*10
                }
                deferredList.add(deferred)
            }

            val results = deferredList.awaitAll()

            // Update UI with results
            updateUI(results, elapsedTime)

            // Delay for the next batch
            delay(8000)
        }

        // Stop the service when processing is complete
        stopSelf()
    }

    private fun updateUI(results: List<Double>, elapsedTime: Long) {
        // Implement logic to update UI with channel values
        // You can use a broadcast, EventBus, or other methods to communicate with the UI
        // Example: Send a broadcast with the results to the main activity
        val intent = Intent("amitapps.media.fetallitesensorapp")
        intent.putExtra("decoded_results", results.toDoubleArray()) // Convert List<Double> to DoubleArray
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdownNow()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
