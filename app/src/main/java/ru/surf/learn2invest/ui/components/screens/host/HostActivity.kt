package ru.surf.learn2invest.ui.components.screens.host

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivityHostBinding

/**
 * Главный экран приложения с BottomBar
 */
@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.navigationBarColor = ContextCompat.getColor(
            this,
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                R.color.accent_background_dark
            } else {
                R.color.accent_background
            }
        )

        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.host_container_view) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}