package com.danimt.appsenderismo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog; // Importante para el diálogo
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danimt.appsenderismo.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class DetalleRutaActivity extends AppCompatActivity {

    private RecyclerView recyclerPuntos;
    private PuntosInteresAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta);

        // Recibimos el objeto Ruta
        Ruta ruta = (Ruta) getIntent().getSerializableExtra("objeto_ruta");

        if (ruta != null) {
            // Referencias UI básica
            TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
            TextView tvDistancia = findViewById(R.id.tvDetalleDistancia);
            TextView tvDificultad = findViewById(R.id.tvDetalleDificultad);
            TextView tvTiempo = findViewById(R.id.tvDetalleTiempo);
            TextView tvDesc = findViewById(R.id.tvDescripcionDetalle);
            // TextView tvCoordenadas = findViewById(R.id.tvDetalleCoordenadas); // Si lo borraste del XML, comenta esto
            CheckBox cbFav = findViewById(R.id.cbFavorito);
            Button btnEliminar = findViewById(R.id.btnEliminarRuta); // Referencia al botón eliminar

            // Asignar valores
            tvTitulo.setText(ruta.getNombre());
            tvDistancia.setText(ruta.getDistancia() + " km");
            tvDificultad.setText(String.valueOf(ruta.getDificultad()));
            tvDesc.setText(ruta.getDescripcion());
            cbFav.setChecked(ruta.isFavorita());
            tvTiempo.setText(ruta.getTiempoEstimado());
            // tvCoordenadas.setText("Lat: " + ruta.getLatitud() + " / Long: " + ruta.getLongitud());

            // --- LÓGICA DE MODIFICACIÓN (Actualizar Favorito) ---
            cbFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ruta.setFavorita(isChecked);
                actualizarRutaEnBD(ruta); // Guardamos el cambio en tiempo real
            });

            // --- LÓGICA DE BORRADO ---
            btnEliminar.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar Ruta")
                        .setMessage("¿Estás seguro de que quieres borrar esta ruta y sus puntos de interés?")
                        .setPositiveButton("Sí", (dialog, which) -> eliminarRuta(ruta))
                        .setNegativeButton("No", null)
                        .show();
            });

            // --- CONFIGURACIÓN DE PUNTOS DE INTERÉS ---

            // 1. Configurar RecyclerView
            recyclerPuntos = findViewById(R.id.recyclerPuntosInteres);
            recyclerPuntos.setLayoutManager(new LinearLayoutManager(this));

            // Inicializamos adapter vacío
            adapter = new PuntosInteresAdapter(new ArrayList<>());
            recyclerPuntos.setAdapter(adapter);

            // 2. Cargar datos de BD en segundo plano
            cargarPuntosDeInteres(ruta.getId());
        }
    }

    // Método para cargar puntos (Lectura)
    private void cargarPuntosDeInteres(int rutaId) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                List<PuntoInteres> puntos = db.rutaDao().getPuntosDeRuta(rutaId);

                runOnUiThread(() -> {
                    if (puntos != null && !puntos.isEmpty()) {
                        adapter.setPuntos(puntos);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al cargar puntos", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    // Método para eliminar (Borrado)
    private void eliminarRuta(Ruta ruta) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                db.rutaDao().delete(ruta); // Borra la ruta y sus puntos (por el Cascade)

                runOnUiThread(() -> {
                    Toast.makeText(this, "Ruta eliminada correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cerramos la actividad para volver a la lista
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Método para actualizar favorito (Modificación)
    private void actualizarRutaEnBD(Ruta ruta) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                db.rutaDao().update(ruta); // Requiere que tengas el método @Update en el DAO
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}