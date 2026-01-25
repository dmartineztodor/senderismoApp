package com.danimt.appsenderismo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Asegúrate de que este import coincida con donde tengas tu AppDatabase
import com.danimt.appsenderismo.AppDatabase;

public class AltaRutaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_ruta);

        // Referencias a las vistas
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etLocalizacion = findViewById(R.id.etLocalizacion);
        EditText etDistancia = findViewById(R.id.etDistancia);
        EditText etDescripcion = findViewById(R.id.etDescripcion);
        RadioGroup rgTipo = findViewById(R.id.rgTipo);
        RatingBar rbDificultad = findViewById(R.id.rbDificultad);
        Switch swFavorita = findViewById(R.id.swFavorita);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> {
            // 1. Recogida de datos
            String nombre = etNombre.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
            } else {
                String ubicacion = etLocalizacion.getText().toString();

                // Tipo de ruta (Circular/Lineal)
                String tipo = "Circular"; // Valor por defecto
                int selectedId = rgTipo.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRb = findViewById(selectedId);
                    tipo = selectedRb.getText().toString();
                }

                // Dificultad
                float dificultad = rbDificultad.getRating();

                // Distancia (con control de errores numéricos)
                double distancia = 0.0;
                try {
                    distancia = Double.parseDouble(etDistancia.getText().toString());
                } catch (NumberFormatException e) {
                    distancia = 0.0;
                }

                String descripcion = etDescripcion.getText().toString();
                boolean esFavorita = swFavorita.isChecked();

                // 2. Creación del Objeto Ruta
                Ruta nuevaRuta = new Ruta(nombre, ubicacion, tipo, dificultad, distancia, descripcion, esFavorita);

                // 3. Guardado en Base de Datos (Room) usando un Hilo Secundario
                new Thread(() -> {
                    try {
                        // a) Obtener instancia de la BD
                        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

                        // b) Insertar la ruta y CAPTURAR EL ID generado
                        // IMPORTANTE: Asegúrate de que en RutaDao el método insert devuelva 'long'
                        long idNuevaRuta = db.rutaDao().insert(nuevaRuta);

                        // c) Crear un Punto de Interés de prueba automáticamente
                        PuntoInteres puntoEjemplo = new PuntoInteres();
                        puntoEjemplo.nombre = "Inicio de ruta: " + nombre;
                        puntoEjemplo.latitud = 40.416;  // Coordenadas dummy
                        puntoEjemplo.longitud = -3.703;
                        puntoEjemplo.ruta_id = (int) idNuevaRuta; // Vinculamos el punto a la ruta recién creada

                        // d) Insertar el punto de interés
                        db.rutaDao().insertPunto(puntoEjemplo);

                        // e) Volver al hilo principal para actualizar la UI
                        runOnUiThread(() -> {
                            Toast.makeText(AltaRutaActivity.this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show();
                            finish(); // Cierra la actividad y vuelve a la anterior
                        });

                    } catch (Exception e) {
                        // Gestión de errores en el hilo principal
                        runOnUiThread(() ->
                                Toast.makeText(AltaRutaActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                }).start();
            }
        });
    }
}