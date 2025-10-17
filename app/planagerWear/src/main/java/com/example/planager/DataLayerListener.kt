package com.example.planager

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import androidx.core.content.edit

class DataLayerListener(private val context: Context) : DataClient.OnDataChangedListener {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/user_data") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val username = dataMap.getString("username")
                val highscore = dataMap.getInt("highscore")

                val prefs = context.getSharedPreferences("my_watch_prefs", Context.MODE_PRIVATE)
                prefs.edit {
                    putString("username", username)
                        .putInt("highscore", highscore)
                }
            }
        }
    }
}
