package com.danimt.appsenderismo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PuntosInteresAdapter extends RecyclerView.Adapter<PuntosInteresAdapter.ViewHolder> {
    private List<PuntoInteres> listaPuntos;
    public PuntosInteresAdapter(List<PuntoInteres> listaPuntos) {
        this.listaPuntos = listaPuntos;
    }

    public void setPuntos(List<PuntoInteres> nuevosPuntos) {
        this.listaPuntos = nuevosPuntos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_punto_interes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PuntoInteres punto = listaPuntos.get(position);
        holder.tvNombre.setText(punto.nombre);
        holder.tvCoordenadas.setText("Lat: " + punto.latitud + " / Lon: " + punto.longitud);
    }

    @Override
    public int getItemCount() {
        if (listaPuntos == null) {
            return 0;
        }
        return listaPuntos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvCoordenadas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePunto);
            tvCoordenadas = itemView.findViewById(R.id.tvCoordenadasPunto);
        }
    }
}