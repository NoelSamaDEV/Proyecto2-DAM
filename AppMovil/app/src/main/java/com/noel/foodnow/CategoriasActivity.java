package com.noel.foodnow;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noel.foodnow.adapters.CategoriaAdapter;
import com.noel.foodnow.models.Categoria;
import com.noel.foodnow.network.ApiService;
import com.noel.foodnow.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriasActivity extends AppCompatActivity {
    private RecyclerView recyclerCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        recyclerCategorias = findViewById(R.id.recyclerCategorias);
        // AQUÍ SE PONEN LAS 2 COLUMNAS
        recyclerCategorias.setLayoutManager(new GridLayoutManager(this, 2));

        cargarCategorias();
    }

    private void cargarCategorias() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.obtenerCategorias().enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoriaAdapter adapter = new CategoriaAdapter(response.body(), CategoriasActivity.this, cat -> {
                        Intent i = new Intent(CategoriasActivity.this, ProductosActivity.class);
                        i.putExtra("ID_CATEGORIA", cat.getIdCategoria());
                        startActivity(i);
                    });
                    recyclerCategorias.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {}
        });
    }
}