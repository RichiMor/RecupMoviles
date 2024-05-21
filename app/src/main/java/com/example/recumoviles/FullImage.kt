package com.example.recumoviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.recumoviles.favorites.AppDatabase
import com.example.recumoviles.favorites.Favorito
import com.example.recumoviles.favorites.FavoritoDao
import com.example.recumoviles.favorites.FavoritosActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FullImage : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var btnBack: Button
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var favoritoDao: FavoritoDao
    private lateinit var imageUrl: String
    private lateinit var buttonAddToFavorite: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        // Obtener la URL de la imagen de la intención
        imageUrl = intent.getStringExtra("image") ?: ""

        // Inicializar el DAO de favoritos
        favoritoDao = AppDatabase.getDatabase(this).favoritoDao()

        // Configurar la imagen utilizando Glide
        imageView = findViewById(R.id.myZoomageView)
        Glide.with(this).load(intent.getStringExtra("image")).into(imageView)

        // Configurar el botón de retroceso
        btnBack = findViewById(R.id.buttonBack)
        btnBack.setOnClickListener {
            val intent = Intent(this, Galeria::class.java)
            startActivity(intent)
        }

        // Configurar el botón de añadir a favoritos
        buttonAddToFavorite = findViewById(R.id.buttonAddToFavorite)
        setupFavoriteButton()

        // Configurar la vista BottomNavigationView y su listener
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
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
                    startActivity(Intent(this, FavoritosActivity::class.java))
                    true
                }
                R.id.action_settings -> {
                    startActivity(Intent(this, Ajustes::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Configurar el botón de favoritos
    private fun setupFavoriteButton() {
        CoroutineScope(Dispatchers.IO).launch {
            // Verificar si la imagen actual está en favoritos
            val isCurrentlyFavorite = favoritoDao.getFavoritos().any { it.imageUrl == imageUrl }

            // Actualizar la imagen del botón de favoritos según su estado actual
            withContext(Dispatchers.Main) {
                if (isCurrentlyFavorite) {
                    buttonAddToFavorite.setImageResource(R.drawable.ic_favorite)
                } else {
                    buttonAddToFavorite.setImageResource(R.drawable.ic_favorite_border)
                }
            }
        }

        // Configurar el listener para el botón de favoritos
        buttonAddToFavorite.setOnClickListener {
            toggleFavoriteState()
        }
    }

    // Cambiar el estado de favoritos al hacer clic en el botón
    private fun toggleFavoriteState() {
        CoroutineScope(Dispatchers.IO).launch {
            // Verificar si la imagen actual está en favoritos
            val isCurrentlyFavorite = favoritoDao.getFavoritos().any { it.imageUrl == imageUrl }

            if (isCurrentlyFavorite) {
                // Si está en favoritos, eliminar de favoritos
                favoritoDao.deleteFavorito(imageUrl)
                withContext(Dispatchers.Main) {
                    // Actualizar la imagen del botón y mostrar un mensaje
                    buttonAddToFavorite.setImageResource(R.drawable.ic_favorite_border)
                    Toast.makeText(this@FullImage, "Se eliminó de Favoritos", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                // Si no está en favoritos, agregar a favoritos
                val favorito = Favorito(imageUrl = imageUrl)
                favoritoDao.insertFavorito(favorito)
                withContext(Dispatchers.Main) {
                    // Actualizar la imagen del botón y mostrar un mensaje
                    buttonAddToFavorite.setImageResource(R.drawable.ic_favorite)
                    Toast.makeText(this@FullImage, "Se añadió a Favoritos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}