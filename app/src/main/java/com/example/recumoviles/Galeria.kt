package com.example.recumoviles

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recumoviles.api.UtilidadesApi
import com.example.recumoviles.favorites.AppDatabase
import com.example.recumoviles.favorites.FavoritoDao
import com.example.recumoviles.favorites.FavoritosActivity
import com.example.recumoviles.modelo.ModeloBuscador
import com.example.recumoviles.modelo.ModeloImagen
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class Galeria : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var listaImagenes: ArrayList<ModeloImagen> = ArrayList()
    private lateinit var manager: GridLayoutManager
    private lateinit var adapter: ImageAdapter
    private lateinit var favoritoDao: FavoritoDao
    private var page: Int = 1
    private var pageSize: Int = 30
    private var isLoading by Delegates.notNull<Boolean>()
    private var isLastPage by Delegates.notNull<Boolean>()
    private lateinit var bottomNavigationView: BottomNavigationView
    private var favoritosList: MutableList<Boolean> = mutableListOf() // Lista de estados de favoritos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galeria)

        favoritoDao = AppDatabase.getDatabase(this).favoritoDao()

        recyclerView = findViewById(R.id.recyclerView)
        adapter = ImageAdapter(this, listaImagenes, favoritoDao, favoritosList) // Pasar la lista de favoritos al adaptador
        manager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter

        isLoading = false
        isLastPage = false

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = manager.childCount
                val totalItemCount = manager.itemCount
                val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= pageSize) {
                        page++
                        getData()
                    }
                }
            }
        })

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, MenuActivity::class.java))
                    true
                }
                R.id.action_gallery -> {
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

        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        val search = menu?.findItem(R.id.search)?.actionView as? SearchView

        search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })


        // Listener para detectar la expansión y contracción del SearchView
        search?.setOnSearchClickListener {
            // Ocultar el menú cuando se abre el teclado
            bottomNavigationView.visibility = View.GONE
        }

        search?.setOnCloseListener {
            // Mostrar el menú cuando se cierra el teclado
            bottomNavigationView.visibility = View.VISIBLE
            false
        }
        return true
    }

    fun getData() {
        if (isLoading) return

        isLoading = true
        UtilidadesApi.getApiInterface().getImages(page, pageSize).enqueue(object : Callback<List<ModeloImagen>> {
            override fun onResponse(call: Call<List<ModeloImagen>>, response: Response<List<ModeloImagen>>) {
                isLoading = false
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isNotEmpty()) {
                            val startIndex = listaImagenes.size
                            listaImagenes.addAll(it)
                            adapter.notifyItemRangeInserted(startIndex, it.size)
                            isLastPage = it.size < pageSize
                            // Inicializar la lista de favoritos con el mismo tamaño que la lista de imágenes
                            favoritosList.addAll(List(it.size) { false })
                        }
                    }
                } else {
                    Toast.makeText(this@Galeria, "Failed to load images", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<ModeloImagen>>, t: Throwable) {
                isLoading = false
                Toast.makeText(this@Galeria, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun searchData(query: String) {
        isLoading = true

        UtilidadesApi.getApiInterface().searchImage(query).enqueue(object : Callback<ModeloBuscador> {
            override fun onResponse(call: Call<ModeloBuscador>, response: Response<ModeloBuscador>) {
                isLoading = false
                if (response.isSuccessful) {
                    response.body()?.let {
                        listaImagenes.clear()
                        listaImagenes.addAll(it.resultados)

                        // Calcular el rango de elementos eliminados e insertados
                        val itemsRemoved = adapter.itemCount
                        val itemsInserted = it.resultados.size

                        // Actualizar el RecyclerView con el rango de elementos
                        adapter.notifyItemRangeRemoved(0, itemsRemoved)
                        adapter.notifyItemRangeInserted(0, itemsInserted)

                        // Inicializar la lista de favoritos con el mismo tamaño que la lista de imágenes
                        favoritosList.clear()
                        favoritosList.addAll(List(itemsInserted) { false })
                    }
                } else {
                    Toast.makeText(this@Galeria, "Error al recuperar datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ModeloBuscador>, t: Throwable) {
                isLoading = false
                Toast.makeText(this@Galeria, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}

