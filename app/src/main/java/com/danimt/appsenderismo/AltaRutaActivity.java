package com.danimt.appsenderismo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AltaRutaActivity extends AppCompatActivity {

    private ImageView imgFoto;
    private Uri fotoUri; // Aquí guardaremos la URI real de la foto
    private String currentPhotoPath; // Ruta en texto para la BD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_ruta);

        // Referencias
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etLocalizacion = findViewById(R.id.etLocalizacion);
        EditText etDistancia = findViewById(R.id.etDistancia);
        EditText etDescripcion = findViewById(R.id.etDescripcion);
        EditText etLat = findViewById(R.id.etLatitudAlta);
        EditText etLon = findViewById(R.id.etLongitudAlta);
        RadioGroup rgTipo = findViewById(R.id.rgTipo);
        RatingBar rbDificultad = findViewById(R.id.rbDificultad);
        Switch swFavorita = findViewById(R.id.swFavorita);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        // Referencias Cámara
        imgFoto = findViewById(R.id.imgFotoRuta);
        Button btnTomarFoto = findViewById(R.id.btnTomarFoto);

        // Configurar Launcher de Cámara
        ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        // Si la foto se tomó bien, la cargamos en la imagen
                        imgFoto.setImageURI(fotoUri);
                    } else {
                        currentPhotoPath = null; // Si cancela, borramos la ruta
                        Toast.makeText(this, "Foto cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Botón Cámara
        btnTomarFoto.setOnClickListener(v -> {
            try {
                File photoFile = crearFicheroImagen();
                if (photoFile != null) {
                    // Generar URI segura con FileProvider
                    fotoUri = FileProvider.getUriForFile(this,
                            "com.danimt.appsenderismo.fileprovider",
                            photoFile);

                    // Lanzar cámara
                    cameraLauncher.launch(fotoUri);
                }
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear fichero de imagen", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Guardar
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
            } else {
                String ubicacion = etLocalizacion.getText().toString();
                String tipo = "Circular";
                int selectedId = rgTipo.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRb = findViewById(selectedId);
                    tipo = selectedRb.getText().toString();
                }

                float dificultad = rbDificultad.getRating();
                double distancia = 0.0;
                try {
                    distancia = Double.parseDouble(etDistancia.getText().toString());
                } catch (NumberFormatException e) { distancia = 0.0; }

                String descripcion = etDescripcion.getText().toString();
                boolean esFavorita = swFavorita.isChecked();

                double lat = 37.99;
                double lon = -1.13;
                try {
                    lat = Double.parseDouble(etLat.getText().toString());
                    lon = Double.parseDouble(etLon.getText().toString());
                } catch (Exception e) {}

                // Guardar la URI como texto (si no hay foto, guardamos null o cadena vacía)
                String uriGuardar = (currentPhotoPath != null) ? currentPhotoPath : "";

                // Crear objeto con la IMAGEN
                Ruta nuevaRuta = new Ruta(nombre, ubicacion, tipo, dificultad, distancia, descripcion, esFavorita, lat, lon, uriGuardar);

                new Thread(() -> {
                    try {
                        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                        db.rutaDao().insert(nuevaRuta);
                        runOnUiThread(() -> {
                            Toast.makeText(AltaRutaActivity.this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(AltaRutaActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }).start();
            }
        });
    }

    // Método auxiliar para crear un fichero único donde guardar la foto
    private File crearFicheroImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Guardamos la ruta absoluta para meterla luego en la BD
        currentPhotoPath = "file://" + image.getAbsolutePath();
        return image;
    }
}