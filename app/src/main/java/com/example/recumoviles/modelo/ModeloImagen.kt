package com.example.recumoviles.modelo

class ModeloImagen (urls:ModeloUrl) {
    var urls:ModeloUrl = urls
        get() = field
        set(value) {
            field = value
        }
    var isFavorite: Boolean = false
}