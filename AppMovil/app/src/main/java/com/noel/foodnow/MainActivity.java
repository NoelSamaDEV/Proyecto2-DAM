package com.noel.foodnow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

// IMPORTANTE: Asegúrate de que esta importación apunte a donde creaste el archivo
import com.noel.foodnow.models.CarritoManager;

public class MainActivity extends AppCompatActivity {

    // 1. CONFIGURACIÓN DEL LANZADOR DEL ESCÁNER
    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    procesarResultadoQR(result.getContents());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- BOTÓN PRINCIPAL: ESCANEAR ---
        Button btnEscanear = findViewById(R.id.btnEscanear);
        btnEscanear.setOnClickListener(v -> {
            iniciarEscaneo();
        });

        // --- BOTÓN SECUNDARIO: DEBUG (Para saltar sin escanear) ---
        // Este botón es útil mientras programas en casa sin QRs a mano
        Button btnDebug = findViewById(R.id.btnDebug);
        if (btnDebug != null) { // Verificamos que exista en el XML
            btnDebug.setOnClickListener(v -> {
                // Simulamos que es la Mesa 1
                CarritoManager.getInstance().setIdMesa(1);
                Toast.makeText(this, "Modo Debug: Mesa 1", Toast.LENGTH_SHORT).show();
                navegarACategorias();
            });
        }
    }

    // 2. MÉTODO PARA ABRIR LA CÁMARA
    private void iniciarEscaneo() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Enfoca el código QR de la mesa");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        // Usamos la clase que creamos para que salga en vertical
        options.setCaptureActivity(CaptureActivityPortrait.class);
        qrLauncher.launch(options);
    }

    // 3. LÓGICA INTELIGENTE PARA PROCESAR EL TEXTO DEL QR
    private void procesarResultadoQR(String contenidoQR) {
        try {
            int idMesa;

            // Si es una URL (ej: "https://foodnow.app/mesa/1"), la recortamos
            if (contenidoQR.contains("/")) {
                String[] partes = contenidoQR.split("/");
                // Cogemos el último trozo después de la última barra
                String ultimoTrozo = partes[partes.length - 1];
                idMesa = Integer.parseInt(ultimoTrozo);
            } else {
                // Si el QR es solo el número "1"
                idMesa = Integer.parseInt(contenidoQR);
            }

            // Guardamos la mesa en la "Mochila" (Singleton)
            CarritoManager.getInstance().setIdMesa(idMesa);

            Toast.makeText(this, "¡Mesa " + idMesa + " detectada!", Toast.LENGTH_SHORT).show();

            // Vamos a la siguiente pantalla
            navegarACategorias();

        } catch (NumberFormatException e) {
            // Si escaneas algo que no acaba en número
            Toast.makeText(this, "QR inválido. No se detectó un número de mesa.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar el QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 4. NAVEGACIÓN
    private void navegarACategorias() {
        Intent intent = new Intent(MainActivity.this, CategoriasActivity.class);
        startActivity(intent);
        finish(); // Cerramos esta pantalla para que no se pueda volver atrás al escáner
    }
}