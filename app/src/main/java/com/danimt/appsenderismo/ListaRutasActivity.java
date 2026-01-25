package com.danimt.appsenderismo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danimt.appsenderismo.AppDatabase; // Ajusta este import si tu AppDatabase está en otro paquete

import java.util.ArrayList;
import java.util.List;

public class ListaRutasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RutasAdapter adapter;
    private List<Ruta> listaRutas = new ArrayList<>(); // Lista local, ya no es estática

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_rutas);

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRutas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el adaptador con una lista vacía por ahora
        adapter = new RutasAdapter(listaRutas, ruta -> {
            Intent intent = new Intent(ListaRutasActivity.this, DetalleRutaActivity.class);
            intent.putExtra("objeto_ruta", ruta);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Configurar Spinner
        Spinner spinner = findViewById(R.id.spinnerFiltro);
        String[] opciones = {"Todas", "Fácil (< 2.5)", "Media (2.5 - 4)", "Difícil (> 4)"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Filtramos sobre la lista que ya tenemos cargada de la BD
                filtrarRutas(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Usamos onResume para recargar la lista cada vez que volvemos a esta pantalla
    // (por ejemplo, al volver de crear una ruta nueva)
    @Override
    protected void onResume() {
        super.onResume();
        cargarRutasDeBaseDeDatos();
    }

    private void cargarRutasDeBaseDeDatos() {
        new Thread(() -> {
            try {
                // 1. Acceder a la BD
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

                // 2. Consultar todas las rutas
                List<Ruta> rutasDeBd = db.rutaDao().getAllRutas();

                // 3. Actualizar la UI
                runOnUiThread(() -> {
                    listaRutas = rutasDeBd; // Actualizamos la referencia local
                    adapter.setRutas(listaRutas); // Pasamos los datos al adaptador
                    adapter.notifyDataSetChanged(); // Refrescamos la vista

                    // Si el spinner tenía algún filtro seleccionado, habría que reaplicarlo,
                    // pero por simplicidad cargamos todas.
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(ListaRutasActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void filtrarRutas(int opcion) {
        // Importante: Filtramos basándonos en 'listaRutas' que contiene los datos de la BD
        if (listaRutas == null) return;

        List<Ruta> filtradas = new ArrayList<>();
        for (Ruta r : listaRutas) {
            switch (opcion) {
                case 0: // Todas
                    filtradas.add(r);
                    break;
                case 1: // Fácil
                    if (r.getDificultad() < 2.5) filtradas.add(r);
                    break;
                case 2: // Media
                    if (r.getDificultad() >= 2.5 && r.getDificultad() <= 4) filtradas.add(r);
                    break;
                case 3: // Difícil
                    if (r.getDificultad() > 4) filtradas.add(r);
                    break;
            }
        }
        if (adapter != null) {
            adapter.setRutas(filtradas);
            adapter.notifyDataSetChanged();
        }
    }
}