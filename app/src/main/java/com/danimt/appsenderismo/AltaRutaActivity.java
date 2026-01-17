package com.danimt.appsenderismo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AltaRutaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_ruta);

        EditText etNombre = findViewById(R.id.etNombre);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();

            // Comprobamos que el nombre no este vacío
            if (nombre.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}