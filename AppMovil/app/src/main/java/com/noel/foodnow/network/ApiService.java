package com.noel.foodnow.network;

import com.noel.foodnow.models.Producto;
import com.noel.foodnow.models.SolicitudPedidoMovil;
import com.noel.foodnow.models.RespuestaPedido;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("productos")
    Call<List<Producto>> obtenerProductos();

    @POST("pedidos/movil/crear")
    Call<RespuestaPedido> enviarPedido(@Body SolicitudPedidoMovil pedido);
}