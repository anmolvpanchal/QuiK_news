package com.`in`.weareindian.quiknews.Main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.`in`.weareindian.quiknews.R


class ActivitySplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splashTimeout = 1000

        Handler().postDelayed(Runnable {
            startActivity(Intent(this, ActivityHomePage::class.java))
            finish()
        }, splashTimeout.toLong())


    }
}
