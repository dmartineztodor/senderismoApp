package com.danimt.appsenderismo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnView = findViewById(R.id.btnView);
        Button btnHelp = findViewById(R.id.btnHelp);
        Button btnAbout = findViewById(R.id.btnAbout);
        Button btnExit = findViewById(R.id.btnExit);

        // Ir a la actividad de añadir ruta
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AltaRutaActivity.class);
            startActivity(intent);
        });

        // Ver rutas
        btnView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaRutasActivity.class);
            startActivity(intent);
        });

        // Abrir web
        btnHelp.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://es.wikiloc.com/rutas/senderismo/espana/murcia/totana"));
            startActivity(browserIntent);
        });

        // Ir a Acerca De
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AcercaDeActivity.class);
            startActivity(intent);
        });

        // Salir de la app
        btnExit.setOnClickListener(v -> {
            finishAffinity(); // Cierra todas las actividades y sale de la app
        });
    }
}