package com.hdev.gnews.presenter.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.hdev.gnews.R
import com.hdev.gnews.core.utils.NetworkMonitor
import com.hdev.gnews.databinding.ActivityMainBinding
import com.hdev.gnews.presenter.saved.SavedFragment
import com.hdev.gnews.presenter.sources.SourcesFragment
import com.hdev.gnews.presenter.trends.TrendsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var networkMonitor: NetworkMonitor
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupObserver()
        setupBottomNavigation()
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.navigation_explore -> {
                    replaceFragment(TrendsFragment())
                    true
                }

                R.id.navigation_sources -> {
                    replaceFragment(SourcesFragment())
                    true
                }

                R.id.navigation_saved -> {
                    replaceFragment(SavedFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    /**
     * setup observer for network monitor
     */
    private fun setupObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkMonitor.isOnline.collect { isOnline ->
                    if (isOnline) {
                        dismissOfflineAlert()
                    } else {
                        showOfflineAlert()
                    }
                }
            }
        }
    }

    private fun showOfflineAlert() {
        snackbar = Snackbar.make(
            binding.root, "No connection internet, please check your connection!",
            Snackbar.LENGTH_INDEFINITE, //always show while the connection internet is offline
        ).apply {
            setBackgroundTint(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))
            setTextColor(Color.WHITE)
            setAction("Open Settings") {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            setActionTextColor(Color.YELLOW)
        }
        snackbar?.show()
    }

    private fun dismissOfflineAlert(){
        snackbar?.dismiss()
    }

}