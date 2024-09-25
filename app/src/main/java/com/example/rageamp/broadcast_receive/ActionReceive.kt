package com.example.rageamp.broadcast_receive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.rageamp.service.MusicService
import com.example.rageamp.utils.MUSIC_ACTION
import com.example.rageamp.utils.MUSIC_ACTION_SERVICE

class ActionReceive : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionReceive = intent?.getIntExtra(MUSIC_ACTION, 0)

        val intentService = Intent(context, MusicService::class.java)
        intentService.putExtra(MUSIC_ACTION_SERVICE, actionReceive)

        context?.startService(intentService)
    }
}
