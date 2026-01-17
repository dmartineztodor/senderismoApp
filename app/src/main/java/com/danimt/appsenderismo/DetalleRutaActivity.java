package com.danimt.appsenderismo;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleRutaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta);

        // Recibimos el objeto
        Ruta ruta = (Ruta) getIntent().getSerializableExtra("objeto_ruta");

        if (ruta != null) {
            TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
            TextView tvDistancia = findViewById(R.id.tvDetalleDistancia);
            TextView tvDificultad = findViewById(R.id.tvDetalleDificultad);
            TextView tvTiempo = findViewById(R.id.tvDetalleTiempo);
            TextView tvDesc = findViewById(R.id.tvDescripcionDetalle);
            CheckBox cbFav = findViewById(R.id.cbFavorito);

            tvTitulo.setText(ruta.getNombre());
            tvDistancia.setText(ruta.getDistancia() + " km");
            tvDificultad.setText(String.valueOf(ruta.getDificultad()));
            tvDesc.setText(ruta.getDescripcion());
            cbFav.setChecked(ruta.isFavorita());

            // Calculamos tiempo con el método del POJO
            tvTiempo.setText(ruta.getTiempoEstimado());

            // Listener para marcar/desmarcar favorito
            cbFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ruta.setFavorita(isChecked);
                String msg = isChecked ? "Añadido a favoritos" : "Quitado de favoritos";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        }
    }
}