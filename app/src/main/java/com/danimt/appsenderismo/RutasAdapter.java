package com.danimt.appsenderismo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.RutaViewHolder> {
    public static List<Ruta> listaRutas;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Ruta ruta);
    }

    public RutasAdapter(List<Ruta> listaRutas, OnItemClickListener listener) {
        this.listaRutas = listaRutas;
        this.listener = listener;
    }

    // Método para actualizar la lista al filtrar
    public void setRutas(List<Ruta> nuevasRutas) {
        this.listaRutas = nuevasRutas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruta, parent, false);
        return new RutaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutaViewHolder holder, int position) {
        Ruta ruta = listaRutas.get(position);
        holder.tvNombre.setText(ruta.getNombre());
        holder.tvDistancia.setText(ruta.getDistancia() + " km");
        holder.rbDificultad.setRating(ruta.getDificultad());

        if ("Circular".equals(ruta.getTipo())) {
            holder.ivTipo.setImageResource(android.R.drawable.ic_menu_rotate);
        } else {
            holder.ivTipo.setImageResource(android.R.drawable.ic_menu_directions);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(ruta));
    }

    @Override
    public int getItemCount() {
        return listaRutas.size();
    }

    static class RutaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvDistancia;
        RatingBar rbDificultad;
        ImageView ivTipo;

        public RutaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRuta);
            tvDistancia = itemView.findViewById(R.id.tvDistancia);
            rbDificultad = itemView.findViewById(R.id.rbDificultadItem);
            ivTipo = itemView.findViewById(R.id.ivTipoRuta);
        }
    }
}