package com.danimt.appsenderismo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

// Esta es la única Activity principal. Actúa como contenedor de Fragmentos.
// Implemento la interfaz del fragmento de lista para poder recibir sus eventos (clicks).
public class MainActivity extends AppCompatActivity implements ListaRutasFragment.OnRutaSeleccionadaListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuro la Toolbar nativa de Android para tener el menú de opciones arriba
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mis Rutas");

        // Solo cargo el fragmento inicial si es la primera vez (savedInstanceState == null).
        // Si no hago esta comprobación, al girar la pantalla se duplicarían los fragmentos.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ListaRutasFragment()) // Cargo la lista por defecto
                    .commit();
        }
    }

    // Menu superior(Toolbar)
    // Inflo el menú XML para que aparezca el botón "+"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Gestiono los clics en los botones de la barra superior
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Si pulsan el botón de "Añadir" (el +)
        if (item.getItemId() == R.id.action_add) {
            // Lanzo la Activity de Alta.
            // He decidido usar Activity aquí y no Fragment porque es un formulario independiente.
            Intent intent = new Intent(this, AltaRutaActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Comunicacion entre fragments
    // Este método se ejecuta cuando el Fragmento de Lista nos avisa de un clic
    @Override
    public void onRutaSeleccionada(Ruta ruta) {
        // Uso el método newInstance para crear el fragmento de detalle con los datos
        DetalleRutaFragment detalleFragment = DetalleRutaFragment.newInstance(ruta);

        // Realizo la transaccion para cambiar de pantalla
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detalleFragment) // Cambio Lista por Detalle
                .addToBackStack(null) // Añado a la pila para que el botón "Atrás" funcione
                .commit();
    }
}