package com.danimt.appsenderismo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AltaRutaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_ruta);

        // Referencias
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etLocalizacion = findViewById(R.id.etLocalizacion);
        EditText etDistancia = findViewById(R.id.etDistancia);
        EditText etDescripcion = findViewById(R.id.etDescripcion);
        EditText etLat = findViewById(R.id.etLatitudAlta);
        EditText etLon = findViewById(R.id.etLongitudAlta);
        RadioGroup rgTipo = findViewById(R.id.rgTipo);
        RatingBar rbDificultad = findViewById(R.id.rbDificultad);
        Switch swFavorita = findViewById(R.id.swFavorita);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString().trim();

                if (nombre.isEmpty()) {
                    Toast.makeText(v.getContext(), getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
                } else {
                    String ubicacion = etLocalizacion.getText().toString();
                    String tipo = "Circular";
                    int selectedId = rgTipo.getCheckedRadioButtonId();
                    if (selectedId != -1) {
                        RadioButton selectedRb = findViewById(selectedId);
                        tipo = selectedRb.getText().toString();
                    }

                    float dificultad = rbDificultad.getRating();
                    double distancia = 0.0;
                    try {
                        distancia = Double.parseDouble(etDistancia.getText().toString());
                    } catch (NumberFormatException e) {
                        distancia = 0.0;
                    }

                    String descripcion = etDescripcion.getText().toString();
                    boolean esFavorita = swFavorita.isChecked();

                    // Valores por defecto
                    double lat = 37.99;
                    double lon = -1.13;

                    try {
                        lat = Double.parseDouble(etLat.getText().toString());
                        lon = Double.parseDouble(etLon.getText().toString());
                    } catch (Exception e) {

                    }

                    Ruta nuevaRuta = new Ruta(nombre, ubicacion, tipo, dificultad, distancia, descripcion, esFavorita, lat, lon);

                    new Thread(() -> {
                        try {
                            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                            db.rutaDao().insert(nuevaRuta);
                            runOnUiThread(() -> {
                                Toast.makeText(AltaRutaActivity.this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(AltaRutaActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }).start();
                }
            }
        });
    }
}