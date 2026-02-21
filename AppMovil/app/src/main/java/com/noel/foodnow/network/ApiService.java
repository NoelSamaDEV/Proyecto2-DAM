package com.noel.foodnow.network;

import com.noel.foodnow.models.Categoria;
import com.noel.foodnow.models.Producto;
import com.noel.foodnow.models.RespuestaPedido;
import com.noel.foodnow.models.SolicitudPedidoMovil;
import com.noel.foodnow.models.CuentaResponse; // <-- El nuevo modelo

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // ---------------------------------------------------
    // LO QUE YA TENÍAS FUNCIONANDO
    // ---------------------------------------------------

    @GET("api/categorias")
    Call<List<Categoria>> obtenerCategorias();

    @GET("api/productos/categoria/{id}")
    Call<List<Producto>> obtenerProductosPorCategoria(@Path("id") Integer idCategoria);

    @POST("api/pedidos")
    Call<RespuestaPedido> enviarPedido(@Body SolicitudPedidoMovil solicitud);

    @POST("api/mesas/{id}/ayuda")
    Call<Void> pedirAyuda(@Path("id") Integer idMesa);


    // ---------------------------------------------------
    // LO NUEVO PARA LA PANTALLA DE "MI CUENTA"
    // ---------------------------------------------------

    // 1. Descargar el ticket actual de la mesa (lista de productos y total)
    @GET("api/mesas/{id}/ticket")
    Call<CuentaResponse> obtenerCuenta(@Path("id") Integer idMesa);

    // 2. Avisar al camarero de que la mesa quiere pagar
    @POST("api/mesas/{id}/cuenta")
    Call<Void> pedirCuenta(@Path("id") Integer idMesa);
}