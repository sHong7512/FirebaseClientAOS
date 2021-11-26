package com.shong.practice_firebaseclient

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName + "_sHong"
    private val firebaseAnalytics: FirebaseAnalytics by lazy { FirebaseAnalytics.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(intent.getBooleanExtra("open_fcm",false))
            firebaseAnalytics.logEvent("opened_fcm", null)

        var token = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG + "_FCMToken", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            } else {
                token = task.result.toString()
            }
        })

        val storedFCMTokenTextView = findViewById<TextView>(R.id.storedFCMTokenTextView)
        val FCMTokenTextView = findViewById<TextView>(R.id.tokenTextView)
        val remoteConfigTextView = findViewById<TextView>(R.id.remoteConfigTextView)

        storedFCMTokenTextView.text = "StoredToken: ${getStoredToken()}"

        findViewById<Button>(R.id.getFCMTokenButton).setOnClickListener {
            FCMTokenTextView.text = "CuttentToken: $token"
            Log.d(TAG,"fcm token -> $token")
        }

        findViewById<Button>(R.id.pushAnalyticsButton).setOnClickListener {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "test id")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "test name")
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "test content type")
            bundle.putInt(FirebaseAnalytics.Param.VALUE, 0)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
        }

        findViewById<Button>(R.id.getRemoteConfigButton).setOnClickListener {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
//                minimumFetchIntervalInSeconds = 3600
                minimumFetchIntervalInSeconds = 0   //실제 사용시에는 3600이상으로 설정해줘야 문제 없이 동작
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            //Default xml을 설정해주려면
//            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

            remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(
                        this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                    val str = remoteConfig.getString("testRemote")

                    Log.d(TAG, "remote test : $str")
                    remoteConfigTextView.text = str

                    //번들에 담아서 이벤트 로깅
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.VALUE, str)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

                } else {
                    Toast.makeText(
                        this, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    fun getStoredToken(): String {
        val pref = applicationContext.getSharedPreferences("test", Context.MODE_PRIVATE)
        Log.d(TAG + "_FCMToken", "stored Token : ${pref.getString("FCMToken", "")!!}")

        return pref.getString("FCMToken", "x")!!
    }

    override fun onStart() {
        super.onStart()
        val eventName = "main_activity_ready"

        //이벤트 트리거하여 인앱메세지 받아오기
        firebaseAnalytics.logEvent(eventName, null)
        FirebaseInAppMessaging.getInstance().triggerEvent(eventName)

        //인앱 메세지 그냥 띄워주기
//        var firebaseInAppMessagingDisplay = FirebaseInAppMessagingDisplay { inAppMessage, cb ->
//            Log.e(TAG, "Display Message callback invoked")
//        }
//        FirebaseInAppMessaging.getInstance().setMessageDisplayComponent(firebaseInAppMessagingDisplay)
    }
}