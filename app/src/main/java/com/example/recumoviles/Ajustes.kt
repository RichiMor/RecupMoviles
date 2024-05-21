package com.example.recumoviles


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.widget.Switch
import com.example.recumoviles.favorites.FavoritosActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Ajustes : AppCompatActivity() {

    private lateinit var swDarkMode: Switch
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajustes)

        // Buscar la vista BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Configurar el listener para el BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    true
                }
                R.id.action_gallery -> {
                    startActivity(Intent(this, Galeria::class.java))
                    true
                }
                R.id.action_favorites -> {
                    // Abrir la actividad Galeria
                    startActivity(Intent(this, FavoritosActivity::class.java))
                    true
                }
                R.id.action_settings -> {
                    true
                }
                else -> false
            }
        }


        swDarkMode = findViewById(R.id.swDarkMode)


        swDarkMode.isChecked = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            else -> false
        }


        swDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
        }
    }

    private fun enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        delegate.applyDayNight()
    }

    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        delegate.applyDayNight()
    }
}
