package com.noel.foodnow.network;

import com.noel.foodnow.models.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @GET("categorias")
    Call<List<Categoria>> obtenerCategorias();

    @GET("productos/categoria/{id}")
    Call<List<Producto>> obtenerProductosPorCategoria(@Path("id") Integer idCategoria);

    @POST("pedidos")
    Call<RespuestaPedido> enviarPedido(@Body SolicitudPedidoMovil solicitud);

    @POST("mesas/{id}/ayuda")
    Call<Void> pedirAyuda(@Path("id") Integer idMesa);

    @GET("mesas/{id}/ticket")
    Call<CuentaResponse> obtenerCuenta(@Path("id") Integer idMesa);

    @POST("mesas/{id}/cuenta")
    Call<Void> pedirCuenta(@Path("id") Integer idMesa);
}