package com.hdev.gnews.presenter.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.hdev.gnews.core.startActivity
import com.hdev.gnews.databinding.ActivitySplashScreenBinding
import com.hdev.gnews.presenter.home.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivitySplashScreenBinding
    private var keepShowing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        //hold the splashscreen to not close automatically
        splashScreen.setKeepOnScreenCondition { keepShowing }
        
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        lifecycleScope.launch {
            delay(3000L)
            keepShowing = false
            navigatedToHome()
        }
    }

    private fun navigatedToHome() {
        startActivity<MainActivity>()
        finish()
    }
}
