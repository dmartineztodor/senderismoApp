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

public class ListaRutasFragment extends Fragment {

    private RecyclerView recyclerView;
    private RutasAdapter adapter;
    private OnRutaSeleccionadaListener listener;

    // Interfaz para comunicarse con el Main Activity
    public interface OnRutaSeleccionadaListener {
        void onRutaSeleccionada(Ruta ruta);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnRutaSeleccionadaListener) {
            listener = (OnRutaSeleccionadaListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar OnRutaSeleccionadaListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_lista_rutas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewRutas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configurar el click del adaptador, pasamos null a la lista inicial, luego la cargamos
        adapter = new RutasAdapter(null, ruta -> {
            if (listener != null) {
                listener.onRutaSeleccionada(ruta);
            }
        });
        recyclerView.setAdapter(adapter);

        // Configuración del Spinner (Filtrado)
        Spinner spinner = view.findViewById(R.id.spinnerFiltro);
        String[] opciones = {"Todas", "Fácil (< 2.5)", "Media (2.5 - 4)", "Difícil (> 4)"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opciones);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarRutas(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargamos al volver
        cargarRutas(0);
    }

    private void cargarRutas(int filtro) {
        new Thread(() -> {
            List<Ruta> rutas;
            RutaDao dao = AppDatabase.getDatabase(getContext()).rutaDao();

            // Usamos los nuevos métodos del DAO según el filtro
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

            if (getActivity() != null) {
                // Pasamos la lista final a la UI
                List<Ruta> finalRutas = rutas;
                getActivity().runOnUiThread(() -> {
                    if (finalRutas != null) {
                        adapter.setRutas(finalRutas);
                    }
                });
            }
        }).start();
    }
}