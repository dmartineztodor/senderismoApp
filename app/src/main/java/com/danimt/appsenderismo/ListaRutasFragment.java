package com.danimt.appsenderismo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Fragmento encargado de mostrar el listado, sustituye a la antigua ListaRutasActivity
public class ListaRutasFragment extends Fragment {
    private RecyclerView recyclerView;
    private RutasAdapter adapter;
    private OnRutaSeleccionadaListener listener;

    // Defino esta interfaz para comunicarme con el MainActivity.
    // El fragmento no decide qué pasa al hacer clic, solo "avisa" a la actividad padre.
    public interface OnRutaSeleccionadaListener {
        void onRutaSeleccionada(Ruta ruta);
    }

    // Ciclo de vida: Se ejecuta cuando el fragmento se pega a la Activity
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Compruebo que la Activity implementa la interfaz. Si no, lanzo error
        if (context instanceof OnRutaSeleccionadaListener) {
            listener = (OnRutaSeleccionadaListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar OnRutaSeleccionadaListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflo el diseño XML que preparé para la lista
        return inflater.inflate(R.layout.activity_lista_rutas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewRutas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configuro el adaptador. La lista inicial es null (se carga luego).
        // Uso una lambda para manejar el clic: cuando tocan una ruta, aviso al listener (MainActivity).
        adapter = new RutasAdapter(null, ruta -> {
            if (listener != null) {
                listener.onRutaSeleccionada(ruta);
            }
        });
        recyclerView.setAdapter(adapter);

        // Configuración del Spinner para el filtrado por dificultad
        Spinner spinner = view.findViewById(R.id.spinnerFiltro);
        String[] opciones = {"Todas", "Fácil (< 2.5)", "Media (2.5 - 4)", "Difícil (> 4)"};

        // Adaptador simple para mostrar las opciones del String[] en el Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opciones);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // Listener para detectar cuando el usuario cambia el filtro
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarRutas(position); // Recargo la lista según la selección
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargo siempre al volver (por si se ha borrado una ruta o se ha creado una nueva)
        cargarRutas(0);
    }

    // Método para pedir los datos a la BD en segundo plano
    private void cargarRutas(int filtro) {
        new Thread(() -> {
            List<Ruta> rutas;
            RutaDao dao = AppDatabase.getDatabase(getContext()).rutaDao();

            // Elijo qué consulta hacer al DAO según la opción del Spinner
            switch (filtro) {
                case 1: // Fácil
                    rutas = dao.getRutasFaciles();
                    break;
                case 2: // Media
                    rutas = dao.getRutasMedias();
                    break;
                case 3: // Difícil
                    rutas = dao.getRutasDificiles();
                    break;
                default: // Todas (caso 0)
                    rutas = dao.getAllRutas();
                    break;
            }

            // Para actualizar la pantalla, tengo que volver al hilo principal obligatoriamente
            if (getActivity() != null) {
                List<Ruta> finalRutas = rutas;
                getActivity().runOnUiThread(() -> {
                    if (finalRutas != null) {
                        adapter.setRutas(finalRutas); // Le paso los nuevos datos al adaptador
                    }
                });
            }
        }).start();
    }
}