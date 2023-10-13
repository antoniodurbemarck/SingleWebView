package com.stripchat.livechat.forWeb

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.ValueCallback
import androidx.activity.result.ActivityResult
import com.stripchat.livechat.WebActivity
import java.io.File

class ActivityResultHelper {

    private fun getTaker(uri: Uri): Intent {
        val result = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        result.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        return result
    }

    private fun getFather(): Intent {
        val result = Intent(Intent.ACTION_GET_CONTENT)
        result.addCategory(Intent.CATEGORY_OPENABLE)
        result.type = "*/*"
        return result
    }

    private fun createTempJpgFile(activity: WebActivity): File {
        return File.createTempFile("jpg_temp_file",
            ".jpg",
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
    }

    fun doForString(activity: WebActivity) {
        activity.doForStringInActivity()
    }

    private fun WebActivity.doForStringInActivity() {
        val photoFile = createTempJpgFile(this)
        uriCall = Uri.fromFile(photoFile)
        val intents = IntentContainer(getFather(), getTaker(Uri.fromFile(photoFile)))
        val chooser = intents.makeChooser()
        launcherSecond.launch(chooser)
    }

    fun doForIntent(activity: WebActivity, activityResult: ActivityResult) {
        activity.doForIntentInActivity(activityResult)
    }

    private fun WebActivity.doForIntentInActivity(activityResult: ActivityResult) {
        CallHelper(activityResult, valCall)(this)
    }

    inner class IntentContainer(private val father: Intent, private val taker: Intent) {
        private val takers
            get() = arrayOf(taker)
        fun makeChooser(): Intent {
            val result = Intent(Intent.ACTION_CHOOSER)
            result.putExtra(Intent.EXTRA_INTENT, father)
            result.putExtra(Intent.EXTRA_INITIAL_INTENTS, takers)
            return result
        }
    }

    inner class CallHelper(
        private val activityResult: ActivityResult,
        private val valCall: ValueCallback<Array<Uri>>?
    ) {
        private val data
            get() = activityResult.data
        private val dataString
            get() = activityResult.data?.dataString
        private val resultCode
            get() = activityResult.resultCode
        operator fun invoke(activity: WebActivity) {
            activity.run {
                this@CallHelper.valCall?.run {
                    if (resultCode == -1) {
                        data?.run {
                            this@CallHelper.dataString?.run {
                                good()
                            }
                        } ?: bad()
                    } else {
                        onReceiveValue(null)
                    }
                    valCall = null
                }
            }
        }

        private fun WebActivity.bad() {
            this@CallHelper.valCall?.run {
                if (uriCall != null) {
                    onReceiveValue(arrayOf(uriCall!!))
                } else {
                    onReceiveValue(null)
                }
            } ?: valCallIsNull
        }

        private val valCallIsNull: Nothing
            get() = throw NullPointerException("ValCall is null...")

        private fun good() {
            this@CallHelper.valCall?.run {
                val u = Uri.parse(dataString)
                onReceiveValue(arrayOf(u))
            } ?: valCallIsNull
        }
    }
}