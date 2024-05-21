package com.example.recumoviles.favorites

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favoritos")
data class Favorito(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUrl: String
)