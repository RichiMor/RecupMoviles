package com.example.recumoviles.favorites

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoritoDao {
    @Query("SELECT * FROM favoritos")
    fun getFavoritos(): List<Favorito>

    @Insert//(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavorito(favorito: Favorito)// Insertar la imagen favorita en la base de datos


    @Query("DELETE FROM favoritos WHERE imageUrl = :imageUrl")
    fun deleteFavorito(imageUrl: String)

    @Query("DELETE FROM favoritos")
    suspend fun eliminarTodasLasFavoritas()

}