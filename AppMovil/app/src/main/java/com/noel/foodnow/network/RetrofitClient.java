package com.noel.foodnow.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // 🔴 IMPORTANTE: Esta es la dirección del ordenador en el Wi-Fi
    // Asegúrar de que el puerto 8080 es el correcto (donde corre Spring Boot)
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
