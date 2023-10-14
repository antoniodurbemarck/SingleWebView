package com.stripchat.livechat

import android.content.Context
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DefaultInitializations {
    fun initializeOneSignal(context: Context) {
        OneSignal.initWithContext(context, getOneSignalId)
        CoroutineScope(Dispatchers.Main).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }
    fun initializeFirebase(context: Context) {
        // TODO
    }

    private val getOneSignalId
        get() = "4a4a694e-9f99-4f44-9652-9c358e903749"
}