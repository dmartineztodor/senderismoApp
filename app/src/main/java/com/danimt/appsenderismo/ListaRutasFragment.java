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
            // Acceso a BD
            List<Ruta> rutas = AppDatabase.getDatabase(getContext()).rutaDao().getAllRutas();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (rutas != null) adapter.setRutas(rutas);
                });
            }
        }).start();
    }
}