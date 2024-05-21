package com.example.recumoviles.favorites

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recumoviles.Ajustes
import com.example.recumoviles.Galeria
import com.example.recumoviles.ImageAdapter
import com.example.recumoviles.MenuActivity
import com.example.recumoviles.R
import com.example.recumoviles.modelo.ModeloImagen
import com.example.recumoviles.modelo.ModeloUrl
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


    class FavoritosActivity : AppCompatActivity() {
        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: ImageAdapter
        private lateinit var favoritoDao: FavoritoDao
        private lateinit var btnEliminarFavoritos: ImageButton
        private var favoritosList: MutableList<Boolean> = mutableListOf()
        private lateinit var bottomNavigationView: BottomNavigationView


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_favoritos)

            // Inicializar favoritoDao
            favoritoDao = AppDatabase.getDatabase(this).favoritoDao()

            recyclerView = findViewById(R.id.recyclerViewFavoritos)
            adapter = ImageAdapter(this, ArrayList(),favoritoDao, favoritosList)
            recyclerView.layoutManager = GridLayoutManager(this, 3)
            recyclerView.adapter = adapter

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
                        true
                    }
                    R.id.action_settings -> {
                        startActivity(Intent(this, Ajustes::class.java))
                        true
                    }
                    else -> false
                }
            }

        btnEliminarFavoritos = findViewById(R.id.btnEliminarFavoritos)
        btnEliminarFavoritos.setOnClickListener {
            eliminarTodasLasFavoritas()
        }

            cargarFavoritos()
        }

        private fun cargarFavoritos() {
            CoroutineScope(Dispatchers.IO).launch {
                val favoritos = favoritoDao.getFavoritos()

                withContext(Dispatchers.Main) {
                    val imagenesFavoritas = favoritos.map { favorito ->
                        ModeloImagen(ModeloUrl(favorito.imageUrl))
                    }

                    adapter.actualizarLista(imagenesFavoritas)
                }
            }
        }

        private fun eliminarTodasLasFavoritas() {
            CoroutineScope(Dispatchers.IO).launch {
                favoritoDao.eliminarTodasLasFavoritas()

                // Después de eliminar, cargar las favoritos nuevamente
                cargarFavoritos()

            }
        }

}