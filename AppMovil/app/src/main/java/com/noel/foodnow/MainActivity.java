package com.noel.foodnow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.noel.foodnow.models.CarritoManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referenciamos el botón con el ID exacto del XML (btnScanner)
        Button btnScanner = findViewById(R.id.btnScanner);

        btnScanner.setOnClickListener(v -> {
            // Iniciamos el escáner de QR
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Escanea el código de tu mesa");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.setOrientationLocked(false);
            integrator.setCaptureActivity(CaptureActivityPortrait.class);
            integrator.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                // Supongamos que el QR contiene el ID de la mesa directamente (ej: "1")
                try {
                    String mesaIdStr = result.getContents();
                    // Si el QR es una URL (ej: foodnow.app/mesa/1), extraemos el final
                    if(mesaIdStr.contains("/")) {
                        mesaIdStr = mesaIdStr.substring(mesaIdStr.lastIndexOf("/") + 1);
                    }

                    int idMesa = Integer.parseInt(mesaIdStr);

                    // Guardamos la mesa en el CarritoManager
                    CarritoManager.getInstance().setIdMesa(idMesa);

                    // Navegamos a las categorías
                    Intent intent = new Intent(MainActivity.this, CategoriasActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "QR no válido para FoodNow", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}