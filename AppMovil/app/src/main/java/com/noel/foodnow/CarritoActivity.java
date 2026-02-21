package com.noel.foodnow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noel.foodnow.adapters.CarritoAdapter;
import com.noel.foodnow.models.CarritoManager;
import com.noel.foodnow.models.RespuestaPedido;
import com.noel.foodnow.models.SolicitudPedidoMovil;
import com.noel.foodnow.models.ProductoCarrito; // <-- Asegúrate de que esta clase exista en models
import com.noel.foodnow.network.ApiService;
import com.noel.foodnow.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerCarrito;
    private CarritoAdapter adapter;
    private TextView txtTotalPedido;
    private Button btnEnviarPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        recyclerCarrito = findViewById(R.id.recyclerCarrito);
        txtTotalPedido = findViewById(R.id.txtTotalPedido);
        btnEnviarPedido = findViewById(R.id.btnEnviarPedido);

        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CarritoAdapter(CarritoManager.getInstance().getCarrito(), new CarritoAdapter.OnCarritoCambiadoListener() {
            @Override
            public void onTotalCambiado() {
                actualizarTotal();
            }
        });

        recyclerCarrito.setAdapter(adapter);
        actualizarTotal();

        btnEnviarPedido.setOnClickListener(v -> {
            if (CarritoManager.getInstance().getCarrito().isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            } else {
                enviarPedidoServidor();
            }
        });
    }

    private void actualizarTotal() {
        double total = CarritoManager.getInstance().calcularTotal();
        txtTotalPedido.setText(String.format("%.2f €", total));

        if (total == 0) {
            btnEnviarPedido.setEnabled(false);
            btnEnviarPedido.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));
        } else {
            btnEnviarPedido.setEnabled(true);
            btnEnviarPedido.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.verde_corporativo)));
        }
    }

    private void enviarPedidoServidor() {
        Integer idMesa = CarritoManager.getInstance().getIdMesa();
        if (idMesa == null) {
            Toast.makeText(this, "Error: No se ha escaneado ninguna mesa", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Preparamos la lista que nos exige el constructor
        List<ProductoCarrito> listaProductos = new ArrayList<>();

        for (CarritoManager.ProductoPedido p : CarritoManager.getInstance().getCarrito()) {
            // Asumimos que tu clase ProductoCarrito tiene este constructor: (idProducto, cantidad)
            ProductoCarrito pc = new ProductoCarrito(p.getProducto().getIdProducto(), p.getCantidad());
            listaProductos.add(pc);
        }

        // 2. ¡LA SOLUCIÓN! Usamos el constructor exacto que pide el error:
        SolicitudPedidoMovil solicitud = new SolicitudPedidoMovil(idMesa, listaProductos);

        // 3. Enviamos a la API
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<RespuestaPedido> call = api.enviarPedido(solicitud);

        call.enqueue(new Callback<RespuestaPedido>() {
            @Override
            public void onResponse(Call<RespuestaPedido> call, Response<RespuestaPedido> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CarritoActivity.this, "¡Pedido enviado a cocina! 👨‍🍳", Toast.LENGTH_LONG).show();
                    CarritoManager.getInstance().vaciarCarrito();
                    finish();
                } else {
                    Toast.makeText(CarritoActivity.this, "Error al enviar el pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaPedido> call, Throwable t) {
                Toast.makeText(CarritoActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}