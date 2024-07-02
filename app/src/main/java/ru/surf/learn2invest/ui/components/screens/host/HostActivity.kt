package ru.surf.learn2invest.ui.components.screens.host

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.surf.learn2invest.R
import ru.surf.learn2invest.databinding.ActivityHostBinding
import ru.surf.learn2invest.utils.setNavigationBarColor

/**
 * Главный экран приложения с BottomBar
 */
@AndroidEntryPoint
class HostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setNavigationBarColor(
            window,
            this,
            R.color.accent_background,
            R.color.accent_background_dark
        )

        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.host_container_view) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}