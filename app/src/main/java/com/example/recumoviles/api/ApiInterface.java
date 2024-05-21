package com.example.recumoviles.api;

import static com.example.recumoviles.api.UtilidadesApi.API_KEY;

import com.example.recumoviles.modelo.ModeloBuscador;
import com.example.recumoviles.modelo.ModeloImagen;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiInterface {
    @Headers("Authorization: Client-ID " + API_KEY)
    @GET("photos")
    Call<List<ModeloImagen>> getImages(
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @Headers("Authorization: Client-ID " + API_KEY)
    @GET("/search/photos")
    Call<ModeloBuscador> searchImage(
            @Query("query") String query
    );
}
