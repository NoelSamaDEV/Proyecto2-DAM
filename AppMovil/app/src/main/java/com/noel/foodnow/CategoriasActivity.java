package com.noel.foodnow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noel.foodnow.adapters.CategoriaAdapter;
import com.noel.foodnow.models.Categoria;
import com.noel.foodnow.models.CarritoManager;
import com.noel.foodnow.network.ApiService;
import com.noel.foodnow.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriasActivity extends AppCompatActivity {

    private RecyclerView recyclerCategorias;
    private Button btnLlamarCamarero;
    private Button btnPedirCuenta;
    private Button btnVerPedidoCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        recyclerCategorias = findViewById(R.id.recyclerCategorias);
        btnLlamarCamarero = findViewById(R.id.btnLlamarCamarero);
        btnPedirCuenta = findViewById(R.id.btnPedirCuenta);
        btnVerPedidoCategorias = findViewById(R.id.btnVerPedidoCategorias);

        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this));

        cargarCategorias();

        btnLlamarCamarero.setOnClickListener(v -> pedirAyuda());

        btnPedirCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriasActivity.this, CuentaActivity.class);
            startActivity(intent);
        });

        btnVerPedidoCategorias.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriasActivity.this, CarritoActivity.class);
            startActivity(intent);
        });
    }

    private void cargarCategorias() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Categoria>> call = api.obtenerCategorias();

        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoriaAdapter adapter = new CategoriaAdapter(
                            response.body(),
                            CategoriasActivity.this,
                            categoria -> {
                                Intent intent = new Intent(CategoriasActivity.this, ProductosActivity.class);
                                intent.putExtra("ID_CATEGORIA", categoria.getIdCategoria());
                                intent.putExtra("NOMBRE_CATEGORIA", categoria.getNombre());
                                startActivity(intent);
                            }
                    );
                    recyclerCategorias.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                Toast.makeText(CategoriasActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show();
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
                if (response.isSuccessful()) {
                    Toast.makeText(CategoriasActivity.this, "¡Camarero avisado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CategoriasActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}