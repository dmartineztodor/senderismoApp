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
// import com.danimt.appsenderismo.AppDatabase;
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
            // Recogemos los datos
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

                // Distancia
                double distancia = 0.0;
                try {
                    distancia = Double.parseDouble(etDistancia.getText().toString());
                } catch (NumberFormatException e) {
                    distancia = 0.0;
                }

                String descripcion = etDescripcion.getText().toString();
                boolean esFavorita = swFavorita.isChecked();

                // Creación del Objeto Ruta
                Ruta nuevaRuta = new Ruta(nombre, ubicacion, tipo, dificultad, distancia, descripcion, esFavorita);

                // Guardamos en la base de datos (Room) usando un hilo secundario
                new Thread(() -> {
                    try {
                        // Primero obtenemos la instancia de la BD (patrón singleton)
                        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

                        // Insertamos la ruta usando el DAO
                        db.rutaDao().insert(nuevaRuta);

                        // Volvemos al hilo principal para actualizar la interfaz
                        runOnUiThread(() -> {
                            Toast.makeText(AltaRutaActivity.this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show();
                            finish(); // Cierra la actividad y vuelve a la anterior
                        });

                    } catch (Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(AltaRutaActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                }).start();
            }
        });
    }
}