package com.noel.foodnow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noel.foodnow.adapters.ProductoAdapter;
import com.noel.foodnow.models.Producto;
import com.noel.foodnow.models.CarritoManager;
import com.noel.foodnow.network.ApiService;
import com.noel.foodnow.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosActivity extends AppCompatActivity {

    private RecyclerView recyclerProductos;
    private TextView txtTituloFoodNow; // Es el título de la parte blanca
    private Button btnAyudaProductos;
    private Button btnCuentaProductos;
    private Button btnVerCarrito;
    private Integer idCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        // 1. Enlazamos las vistas
        recyclerProductos = findViewById(R.id.recyclerProductos);
        txtTituloFoodNow = findViewById(R.id.txtTituloFoodNow);
        btnAyudaProductos = findViewById(R.id.btnAyudaProductos);
        btnCuentaProductos = findViewById(R.id.btnCuentaProductos);
        btnVerCarrito = findViewById(R.id.btnVerCarrito);

        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        // 2. Recibir datos de la categoría elegida
        idCategoria = getIntent().getIntExtra("ID_CATEGORIA", -1);
        String nombreCategoria = getIntent().getStringExtra("NOMBRE_CATEGORIA");

        // Ponemos el nombre de la categoría en la franja blanca
        if (nombreCategoria != null) {
            txtTituloFoodNow.setText(nombreCategoria);
        }

        // Cargamos los productos si hay una categoría válida
        if (idCategoria != -1) {
            cargarProductos(idCategoria);
        }

        // 3. CONFIGURACIÓN DE LOS BOTONES

        // Botón Ayuda
        btnAyudaProductos.setOnClickListener(v -> pedirAyuda());

        // Botón Cuenta (AQUÍ ESTÁ LA NAVEGACIÓN NUEVA)
        btnCuentaProductos.setOnClickListener(v -> {
            Intent intent = new Intent(ProductosActivity.this, CuentaActivity.class);
            startActivity(intent);
        });

        // Botón Ver Mi Pedido (Carrito)
        btnVerCarrito.setOnClickListener(v -> {
            Intent intent = new Intent(ProductosActivity.this, CarritoActivity.class);
            startActivity(intent);
        });
    }

    private void cargarProductos(Integer idCat) {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Producto>> call = api.obtenerProductosPorCategoria(idCat);

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Inicializamos el adaptador de productos
                    ProductoAdapter adapter = new ProductoAdapter(response.body(), ProductosActivity.this);
                    recyclerProductos.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(ProductosActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pedirAyuda() {
        Integer idMesa = CarritoManager.getInstance().getIdMesa();
        if (idMesa == null) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = api.pedirAyuda(idMesa);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ProductosActivity.this, "¡Camarero avisado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductosActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}