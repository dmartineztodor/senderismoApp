package com.danimt.appsenderismo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements ListaRutasFragment.OnRutaSeleccionadaListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mis Rutas");

        // Cargar el Fragmento de Lista al inicio
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ListaRutasFragment())
                    .commit();
        }
    }

    // Menu superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(this, AltaRutaActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRutaSeleccionada(Ruta ruta) {
        // Crear el fragmento de detalle pasando la ruta
        DetalleRutaFragment detalleFragment = DetalleRutaFragment.newInstance(ruta);

        // Reemplazar el fragmento actual por el de detalle
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detalleFragment)
                .addToBackStack(null) // Para que el botón "Atrás" del móvil funcione
                .commit();
    }
}