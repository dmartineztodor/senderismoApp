package com.danimt.appsenderismo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ListaRutasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RutasAdapter adapter;
    private List<Ruta> todasLasRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_rutas);

        // 1. Datos Ficticios
        todasLasRutas = new ArrayList<>();
        todasLasRutas.add(new Ruta("Ruta del Cares", "Asturias", "Lineal", 4.5f, 12.0, "Espectacular garganta divina .", true));
        todasLasRutas.add(new Ruta("Monte Arabí", "Yecla", "Circular", 2.0f, 5.5, "Ruta sencilla con pinturas rupestres.", false));
        todasLasRutas.add(new Ruta("Sierra Espuña", "Murcia", "Circular", 3.5f, 14.2, "Bosques densos y vistas increíbles.", true));
        todasLasRutas.add(new Ruta("Mulhacén", "Granada", "Lineal", 5.0f, 20.0, "Ascenso al pico más alto de la península.", false));

        // 2. Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRutas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Al hacer clic, abrimos DetalleRutaActivity pasando el objeto
        adapter = new RutasAdapter(todasLasRutas, ruta -> {
            Intent intent = new Intent(ListaRutasActivity.this, DetalleRutaActivity.class);
            intent.putExtra("objeto_ruta", ruta);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // 3. Configurar Spinner
        Spinner spinner = findViewById(R.id.spinnerFiltro);
        String[] opciones = {"Todas", "Fácil (< 2.5)", "Media (2.5 - 4)", "Difícil (> 4)"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarRutas(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void filtrarRutas(int opcion) {
        List<Ruta> filtradas = new ArrayList<>();
        for (Ruta r : todasLasRutas) {
            switch (opcion) {
                case 0:
                    filtradas.add(r);
                    break;
                case 1:
                    if (r.getDificultad() < 2.5) filtradas.add(r);
                    break;
                case 2:
                    if (r.getDificultad() >= 2.5 && r.getDificultad() <= 4) filtradas.add(r);
                    break;
                case 3:
                    if (r.getDificultad() > 4) filtradas.add(r);
                    break;
            }
        }
        adapter.setRutas(filtradas);
    }
}