package com.example.recumoviles

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recumoviles.favorites.Favorito
import com.example.recumoviles.favorites.FavoritoDao
import com.example.recumoviles.modelo.ModeloImagen

class ImageAdapter(
    private val context: Context,
    private val lista: ArrayList<ModeloImagen>,
    private val favoritoDao: FavoritoDao,
    private val favoritosList: MutableList<Boolean>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.image_item_layout, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // Cargar la imagen usando Glide
        Glide.with(context).load(lista[position].urls.regular).into(holder.imageView)

        // Configurar el click listener para abrir la imagen completa en FullImage Activity
        holder.imageView.setOnClickListener {
            val intent = Intent(context, FullImage::class.java)
            intent.putExtra("image", lista[position].urls.regular)
            context.startActivity(intent)
        }

        // Verificar si la lista de favoritos tiene elementos antes de acceder a una posición específica
        if (favoritosList.isNotEmpty() && position < favoritosList.size) {
            // Actualizar el icono de favoritos según el estado de favoritos en la posición actual
            if (favoritosList[position]) {
                holder.favoriteIcon.setImageResource(R.drawable.ic_favorite)
            } else {
                holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_border)
            }

            // Configurar el click listener para cambiar el estado de favoritos
            holder.favoriteIcon.setOnClickListener {
                toggleFavoriteState(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun actualizarLista(nuevaLista: List<ModeloImagen>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    private fun toggleFavoriteState(position: Int) {
        favoritosList[position] = !favoritosList[position] // Cambiar el estado de favoritos en la posición actual
        if (favoritosList[position]) {
            // Si se convierte en favorito, guardar en la base de datos
            val favorito = Favorito(imageUrl = lista[position].urls.regular, id = 0)
            favoritoDao.insertFavorito(favorito)
        } else {
            // Si se elimina de favoritos, eliminar de la base de datos
            favoritoDao.deleteFavorito((lista[position].urls.regular))
        }
        notifyDataSetChanged() // Notificar al adaptador sobre el cambio en la lista de favoritos
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var favoriteIcon: ImageView = itemView.findViewById(R.id.imageView3)
    }
}
