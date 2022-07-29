package com.mbass.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.mbass.myapplication.util.SharedPrefs

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        object : CountDownTimer(1000, 500) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (!SharedPrefs.getToken(this@SplashActivity).equals("")) {
                    val intent = Intent(this@SplashActivity, RoomListActivity::class.java)
                    intent.putExtra("authorization", SharedPrefs.getToken(this@SplashActivity))
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }
}