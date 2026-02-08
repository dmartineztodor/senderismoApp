package com.danimt.appsenderismo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class DetalleRutaFragment extends Fragment {
    private static final String ARG_RUTA = "ruta";
    private Ruta ruta;
    private RecyclerView recyclerPuntos;
    private PuntosInteresAdapter adapter;

    public static DetalleRutaFragment newInstance(Ruta ruta) {
        DetalleRutaFragment fragment = new DetalleRutaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RUTA, ruta);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ruta = (Ruta) getArguments().getSerializable(ARG_RUTA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalle_ruta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ruta != null) {
            // Referencias
            TextView tvTitulo = view.findViewById(R.id.tvTituloDetalle);
            TextView tvDistancia = view.findViewById(R.id.tvDetalleDistancia);
            TextView tvDificultad = view.findViewById(R.id.tvDetalleDificultad);
            TextView tvTiempo = view.findViewById(R.id.tvDetalleTiempo);
            TextView tvDesc = view.findViewById(R.id.tvDescripcionDetalle);
            TextView tvCoords = view.findViewById(R.id.tvDetalleCoordenadas);
            CheckBox cbFav = view.findViewById(R.id.cbFavorito);
            Button btnEliminar = view.findViewById(R.id.btnEliminarRuta);
            Button btnMapa = view.findViewById(R.id.btnVerMapa);
            FloatingActionButton fab = view.findViewById(R.id.fabAddPunto);

            // Valores
            tvTitulo.setText(ruta.getNombre());
            tvDistancia.setText(ruta.getDistancia() + " km");
            tvDificultad.setText(String.valueOf(ruta.getDificultad()));
            tvTiempo.setText(ruta.getTiempoEstimado());
            tvDesc.setText(ruta.getDescripcion());
            String coordsTexto = "Lat: " + ruta.getLatitud() + " / Lon: " + ruta.getLongitud();
            tvCoords.setText(coordsTexto);
            cbFav.setChecked(ruta.isFavorita());

            // Listeners

            // Boton Mapa
            btnMapa.setOnClickListener(v -> abrirGoogleMaps());

            // Checkbox Favorito
            cbFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ruta.setFavorita(isChecked);
                actualizarRutaEnBD();
            });

            // Botón Eliminar
            btnEliminar.setOnClickListener(v -> confirmarBorrado());

            // Botón Flotante (Añadir Punto)
            fab.setOnClickListener(v -> mostrarDialogoAñadir());

            // Configurar Lista de Puntos
            recyclerPuntos = view.findViewById(R.id.recyclerPuntosInteres);
            recyclerPuntos.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new PuntosInteresAdapter(new ArrayList<>());
            recyclerPuntos.setAdapter(adapter);

            // Referencia a la imagen de cabecera
            ImageView imgCabecera = view.findViewById(R.id.imgCabecera);

            // Lógica para cargar la foto si existe
            if (ruta.getImagenUri() != null && !ruta.getImagenUri().isEmpty()) {
                // Si hay foto guardada, la mostramos
                imgCabecera.setImageURI(Uri.parse(ruta.getImagenUri()));
                // Ajustamos la escala para que se vea bonita
                imgCabecera.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                // Si no hay foto, dejamos la de por defecto
                imgCabecera.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Cargar puntos de la BD
            cargarPuntos();
        }
    }

    // Métodos
    private void abrirGoogleMaps() {
        // Creamos la uri con las coordenadas de la ruta y una etiqueta con el nombre
        Uri gmmIntentUri = Uri.parse("geo:" + ruta.getLatitud() + "," + ruta.getLongitud() + "?q=" + ruta.getLatitud() + "," + ruta.getLongitud() + "(" + Uri.encode(ruta.getNombre()) + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            // Si no tiene Maps instalado, abrimos con el navegador u otra app de mapas
            startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
        }
    }

    private void confirmarBorrado() {
        // Usamos requireContext() porque estamos en un Fragment
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_msg)
                .setPositiveButton(R.string.btn_si, (d, w) -> borrarRuta())
                .setNegativeButton(R.string.btn_no, null)
                .show();
    }

    private void borrarRuta() {
        new Thread(() -> {
            AppDatabase.getDatabase(getContext()).rutaDao().delete(ruta);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), R.string.btn_eliminar_ruta, Toast.LENGTH_SHORT).show();
                    // Volver atrás
                    getActivity().getSupportFragmentManager().popBackStack();
                });
            }
        }).start();
    }

    private void actualizarRutaEnBD() {
        new Thread(() -> {
            AppDatabase.getDatabase(getContext()).rutaDao().update(ruta);
        }).start();
    }

    private void mostrarDialogoAñadir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_nuevo_punto, null);

        EditText etNombre = view.findViewById(R.id.etNombrePunto);
        EditText etLat = view.findViewById(R.id.etLatitud);
        EditText etLon = view.findViewById(R.id.etLongitud);

        builder.setView(view)
                .setTitle(R.string.title_add_point)
                .setPositiveButton(R.string.btn_guardar, (dialog, id) -> {
                    String nombre = etNombre.getText().toString();
                    String latStr = etLat.getText().toString();
                    String lonStr = etLon.getText().toString();

                    if (!nombre.isEmpty()) {
                        guardarPuntoEnBD(nombre, latStr, lonStr);
                    } else {
                        Toast.makeText(getContext(), R.string.error_missing_name, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.btn_cancelar, (dialog, id) -> dialog.dismiss());

        builder.create().show();
    }

    private void guardarPuntoEnBD(String nombre, String latStr, String lonStr) {
        double lat = latStr.isEmpty() ? 0.0 : Double.parseDouble(latStr);
        double lon = lonStr.isEmpty() ? 0.0 : Double.parseDouble(lonStr);

        new Thread(() -> {
            PuntoInteres nuevoPunto = new PuntoInteres();
            nuevoPunto.nombre = nombre;
            nuevoPunto.latitud = lat;
            nuevoPunto.longitud = lon;
            nuevoPunto.ruta_id = ruta.getId(); // Vinculamos con la ID de la ruta actual

            AppDatabase.getDatabase(getContext()).rutaDao().insertPunto(nuevoPunto);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), R.string.msg_point_added, Toast.LENGTH_SHORT).show();
                    cargarPuntos(); // Recargamos la lista
                });
            }
        }).start();
    }

    private void cargarPuntos() {
        new Thread(() -> {
            List<PuntoInteres> puntos = AppDatabase.getDatabase(getContext()).rutaDao().getPuntosDeRuta(ruta.getId());
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setPuntos(puntos));
            }
        }).start();
    }
}