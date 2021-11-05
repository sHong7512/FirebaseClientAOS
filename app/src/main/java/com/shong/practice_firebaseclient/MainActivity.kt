package com.shong.practice_firebaseclient

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName + "_sHong"

    lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        var token = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG + "_FCMToken", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }else{
                token = task.result.toString()
            }
        })

        val storedFCMTokenTextView = findViewById<TextView>(R.id.storedFCMTokenTextView)
        val FCMTokenTextView = findViewById<TextView>(R.id.tokenTextView)
        val remoteConfigTextView = findViewById<TextView>(R.id.remoteConfigTextView)

        storedFCMTokenTextView.text = "StoredToken: ${getStoredToken()}"

        findViewById<Button>(R.id.getFCMTokenButton).setOnClickListener{
            FCMTokenTextView.text = "CuttentToken: $token"
        }

        findViewById<Button>(R.id.pushAnalyticsButton).setOnClickListener {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "test id")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "test name")
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "test content type")
            bundle.putInt(FirebaseAnalytics.Param.VALUE, 0)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        }

        findViewById<Button>(R.id.getRemoteConfigButton).setOnClickListener {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
//                minimumFetchIntervalInSeconds = 3600
                minimumFetchIntervalInSeconds = 0
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
//            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

            remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT).show()
                    val str = remoteConfig.getString("testRemote")

                    Log.d(TAG,"remote test : $str")
                    remoteConfigTextView.text = str
                } else {
                    Toast.makeText(this, "Fetch failed",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun getStoredToken() : String{
        val pref = applicationContext.getSharedPreferences("test", Context.MODE_PRIVATE)
        Log.d(TAG + "_FCMToken", "stored Token : ${pref.getString("FCMToken", "")!!}")

        return pref.getString("FCMToken", "x")!!
    }

    override fun onStart() {
        super.onStart()
        val eventName = "main_activity_ready"

        FirebaseAnalytics.getInstance(this).logEvent(eventName,null)
        FirebaseInAppMessaging.getInstance().triggerEvent(eventName)

//        var firebaseInAppMessagingDisplay = FirebaseInAppMessagingDisplay { inAppMessage, cb ->
//            Log.e(TAG, "Display Message callback invoked")
//        }
//        FirebaseInAppMessaging.getInstance().setMessageDisplayComponent(firebaseInAppMessagingDisplay)
    }
}