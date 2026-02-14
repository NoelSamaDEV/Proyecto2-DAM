package com.noel.foodnow.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // 🔴 IMPORTANTE: Esta es la dirección de tu ordenador en tu Wi-Fi
    // Asegúrate de que el puerto 8080 es el correcto (donde corre tu Spring Boot)
    private static final String BASE_URL = "http://192.168.0.103:8080/api/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
