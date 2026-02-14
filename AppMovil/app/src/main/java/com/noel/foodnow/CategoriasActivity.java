package com.noel.foodnow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.noel.foodnow.adapters.CategoriaAdapter;
import com.noel.foodnow.models.Categoria;
import java.util.ArrayList;
import java.util.List;

public class CategoriasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        RecyclerView recycler = findViewById(R.id.recyclerCategorias);

        // Configuramos Grid de 2 Columnas
        recycler.setLayoutManager(new GridLayoutManager(this, 2));

        // DATOS DE PRUEBA (Aquí pondrías tus URLs reales en el futuro)
        List<Categoria> datos = new ArrayList<>();
        datos.add(new Categoria(1, "Entrantes", "")); // URL vacía -> Icono Gris
        datos.add(new Categoria(2, "Carnes", "https://via.placeholder.com/150")); // URL de prueba
        datos.add(new Categoria(3, "Bebidas", ""));
        datos.add(new Categoria(4, "Postres", ""));

        // Adaptador
        CategoriaAdapter adapter = new CategoriaAdapter(datos, this, categoria -> {
            // AL HACER CLIC:
            Toast.makeText(this, "Elegiste: " + categoria.getNombre(), Toast.LENGTH_SHORT).show();

            // Aquí navegaremos a la lista de Productos filtrada (Siguiente paso)
            // Intent intent = new Intent(CategoriasActivity.this, ProductosActivity.class);
            // intent.putExtra("ID_CATEGORIA", categoria.getId());
            // startActivity(intent);
        });

        recycler.setAdapter(adapter);
    }
}