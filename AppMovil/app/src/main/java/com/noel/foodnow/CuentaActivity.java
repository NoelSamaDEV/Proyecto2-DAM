package com.noel.foodnow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noel.foodnow.adapters.CuentaAdapter;
import com.noel.foodnow.models.CarritoManager;
import com.noel.foodnow.models.CuentaResponse;
import com.noel.foodnow.network.ApiService;
import com.noel.foodnow.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CuentaActivity extends AppCompatActivity {

    private RecyclerView recyclerCuenta;
    private TextView txtTotalCuenta;
    private Button btnPedirCuentaDefinitivo;
    private CuentaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);

        // Enlazamos con los IDs del XML
        recyclerCuenta = findViewById(R.id.recyclerCuenta);
        txtTotalCuenta = findViewById(R.id.txtTotalCuenta);
        btnPedirCuentaDefinitivo = findViewById(R.id.btnPedirCuentaDefinitivo);

        recyclerCuenta.setLayoutManager(new LinearLayoutManager(this));

        // Cargamos los datos
        obtenerTicketMesa();

        // Configurar el botón de pedir cuenta (avisar al camarero)
        btnPedirCuentaDefinitivo.setOnClickListener(v -> enviarAvisoCuenta());
    }

    private void obtenerTicketMesa() {
        Integer idMesa = CarritoManager.getInstance().getIdMesa();
        if (idMesa == null) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<CuentaResponse> call = api.obtenerCuenta(idMesa);

        call.enqueue(new Callback<CuentaResponse>() {
            @Override
            public void onResponse(Call<CuentaResponse> call, Response<CuentaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CuentaResponse cuenta = response.body();

                    if (cuenta.getLineas() != null) {
                        adapter = new CuentaAdapter(cuenta.getLineas());
                        recyclerCuenta.setAdapter(adapter);
                    }

                    txtTotalCuenta.setText(String.format("%.2f €", cuenta.getTotal()));
                } else {
                    Toast.makeText(CuentaActivity.this, "Aún no has pedido nada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CuentaResponse> call, Throwable t) {
                Toast.makeText(CuentaActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarAvisoCuenta() {
        Integer idMesa = CarritoManager.getInstance().getIdMesa();
        if (idMesa == null) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = api.pedirCuenta(idMesa);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CuentaActivity.this, "¡Camarero avisado! Enseguida te traen la cuenta.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CuentaActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}