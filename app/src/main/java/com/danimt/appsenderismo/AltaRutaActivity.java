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

// Importante: Asegúrate de importar List y ArrayList si hace falta,
// aunque aquí usaremos la referencia directa.
import java.util.ArrayList;

public class AltaRutaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_ruta);

        EditText etNombre = findViewById(R.id.etNombre);
        EditText etLocalizacion = findViewById(R.id.etLocalizacion);
        EditText etDistancia = findViewById(R.id.etDistancia);
        EditText etDescripcion = findViewById(R.id.etDescripcion);
        RadioGroup rgTipo = findViewById(R.id.rgTipo);
        RatingBar rbDificultad = findViewById(R.id.rbDificultad);
        Switch swFavorita = findViewById(R.id.swFavorita);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> {
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

                // Dificultad y distancia
                float dificultad = rbDificultad.getRating();
                double distancia = 0.0;
                try {
                    distancia = Double.parseDouble(etDistancia.getText().toString());
                } catch (NumberFormatException e) {
                    distancia = 0.0; // Si el campo está vacío o mal escrito
                }

                String descripcion = etDescripcion.getText().toString();
                boolean esFavorita = swFavorita.isChecked();

                Ruta nuevaRuta = new Ruta(nombre, ubicacion, tipo, dificultad, distancia, descripcion, esFavorita);

                if (ListaRutasActivity.todasLasRutas == null) {
                    ListaRutasActivity.todasLasRutas = new ArrayList<>();
                }

                ListaRutasActivity.todasLasRutas.add(nuevaRuta);

                Toast.makeText(this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}