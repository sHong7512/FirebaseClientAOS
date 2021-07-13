package com.shong.practice_firebaseclient

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging

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

        storedFCMTokenTextView.text = "StoredToken: ${getStoredToken()}"

        findViewById<Button>(R.id.getFCMTokenButton).setOnClickListener{
            FCMTokenTextView.text = "CuttentToken: $token"
        }

        findViewById<Button>(R.id.pushAnalyticsButton).setOnClickListener {
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "test id")
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "test name")
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "test content type")
            bundle.putString(FirebaseAnalytics.Param.VALUE, "test Value")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
        }
    }

    fun getStoredToken() : String{
        val pref = applicationContext.getSharedPreferences("test", Context.MODE_PRIVATE)
        Log.d(TAG + "_FCMToken", "stored Token : ${pref.getString("FCMToken", "")!!}")

        return pref.getString("FCMToken", "x")!!
    }
}