package com.danimt.appsenderismo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DetalleRutaActivity extends AppCompatActivity {
    private RecyclerView recyclerPuntos;
    private PuntosInteresAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta);

        // Recibimos el objeto 'Ruta'
        Ruta ruta = (Ruta) getIntent().getSerializableExtra("objeto_ruta");

        if (ruta != null) {
            // Referencias
            TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
            TextView tvDistancia = findViewById(R.id.tvDetalleDistancia);
            TextView tvDificultad = findViewById(R.id.tvDetalleDificultad);
            TextView tvTiempo = findViewById(R.id.tvDetalleTiempo);
            TextView tvDesc = findViewById(R.id.tvDescripcionDetalle);
            CheckBox cbFav = findViewById(R.id.cbFavorito);
            Button btnEliminar = findViewById(R.id.btnEliminarRuta);
            FloatingActionButton fab = findViewById(R.id.fabAddPunto); // Botón flotante

            // Asignar valores
            tvTitulo.setText(ruta.getNombre());
            tvDistancia.setText(ruta.getDistancia() + " km");
            tvDificultad.setText(String.valueOf(ruta.getDificultad()));
            tvDesc.setText(ruta.getDescripcion());
            cbFav.setChecked(ruta.isFavorita());
            tvTiempo.setText(ruta.getTiempoEstimado());

            // Favorito
            cbFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ruta.setFavorita(isChecked);
                actualizarRutaEnBD(ruta);
            });

            // Borrar ruta
            btnEliminar.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar Ruta")
                        .setMessage("¿Estás seguro de que quieres borrar esta ruta y sus puntos de interés?")
                        .setPositiveButton("Sí", (dialog, which) -> eliminarRuta(ruta))
                        .setNegativeButton("No", null)
                        .show();
            });

            // Botón flotante (Añadir Puntos)
            fab.setOnClickListener(v -> mostrarDialogoAñadir(ruta.getId()));

            // Configurar RecyclerView
            recyclerPuntos = findViewById(R.id.recyclerPuntosInteres);
            recyclerPuntos.setLayoutManager(new LinearLayoutManager(this));

            adapter = new PuntosInteresAdapter(new ArrayList<>());
            recyclerPuntos.setAdapter(adapter);

            // Cargar datos
            cargarPuntosDeInteres(ruta.getId());
        }
    }

    // Métodos
    private void mostrarDialogoAñadir(int rutaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_nuevo_punto, null);

        EditText etNombre = view.findViewById(R.id.etNombrePunto);
        EditText etLat = view.findViewById(R.id.etLatitud);
        EditText etLon = view.findViewById(R.id.etLongitud);

        builder.setView(view)
                .setTitle(R.string.title_add_point)
                .setPositiveButton(R.string.btn_save, (dialog, id) -> {
                    String nombre = etNombre.getText().toString();
                    String latStr = etLat.getText().toString();
                    String lonStr = etLon.getText().toString();

                    if (!nombre.isEmpty()) {
                        guardarPuntoEnBD(nombre, latStr, lonStr, rutaId);
                    } else {
                        Toast.makeText(this, R.string.error_missing_name, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, (dialog, id) -> dialog.dismiss());

        builder.create().show();
    }

    private void guardarPuntoEnBD(String nombre, String latStr, String lonStr, int rutaId) {
        double lat;
        double lon;

        if (!latStr.isEmpty()) {
            lat = Double.parseDouble(latStr);
        } else {
            lat = 0.0;
        }

        if (!lonStr.isEmpty()) {
            lon = Double.parseDouble(lonStr);
        } else {
            lon = 0.0;
        }

        new Thread(() -> {
            try {
                PuntoInteres nuevoPunto = new PuntoInteres();
                nuevoPunto.nombre = nombre;
                nuevoPunto.latitud = lat;
                nuevoPunto.longitud = lon;
                nuevoPunto.ruta_id = rutaId;

                AppDatabase.getDatabase(getApplicationContext()).rutaDao().insertPunto(nuevoPunto);

                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.msg_point_added, Toast.LENGTH_SHORT).show();
                    cargarPuntosDeInteres(rutaId);
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar punto", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void cargarPuntosDeInteres(int rutaId) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                List<PuntoInteres> puntos = db.rutaDao().getPuntosDeRuta(rutaId);

                runOnUiThread(() -> {
                    if (puntos != null) {
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

    private void eliminarRuta(Ruta ruta) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                db.rutaDao().delete(ruta);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Ruta eliminada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void actualizarRutaEnBD(Ruta ruta) {
        new Thread(() -> {
            try {
                AppDatabase.getDatabase(getApplicationContext()).rutaDao().update(ruta);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}