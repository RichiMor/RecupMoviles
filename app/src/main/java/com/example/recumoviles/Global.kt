package com.example.recumoviles

import android.app.Application
import android.util.Log
import com.example.recumoviles.favorites.AppDatabase
import com.example.recumoviles.favorites.FavoritoDao

class Global : Application() {
    private lateinit var favoritoDao: FavoritoDao

    override fun onCreate() {
        super.onCreate()

        //Conectando a la base de datos
        val db = AppDatabase.getDatabase(this)
        favoritoDao = db.favoritoDao()

        // Verificar si la base de datos se inicializó correctamente
        try {
            db.openHelper.writableDatabase
            Log.d("Global", "Database initialized successfully")
        } catch (e: Exception) {
            Log.e("Global", "Failed to initialize database: ${e.message}")
        }
    }
}