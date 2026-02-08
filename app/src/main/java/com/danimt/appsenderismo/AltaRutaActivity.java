package com.danimt.appsenderismo;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
    private Uri fotoUri;
    private String rutaImg; // Guardo la ruta aquí para meterla luego en la BD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_ruta);

        // Enlazo todos los controles de la interfaz
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

        // Controles para la foto
        imgFoto = findViewById(R.id.imgFotoRuta);
        Button btnTomarFoto = findViewById(R.id.btnTomarFoto);

        // Registro el contrato para recibir el resultado de la cámara
        ActivityResultLauncher<Uri> camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        // Si la foto se hizo bien, la cargo en la vista previa
                        imgFoto.setImageURI(fotoUri);
                    } else {
                        rutaImg = null; // Si cancela, no guardo
                        Toast.makeText(this, "Foto cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Listener para abrir la cámara
        btnTomarFoto.setOnClickListener(v -> {
            try {
                // Primero creo un fichero temporal vacío
                File fotoArchivo = crearFicheroImagen();
                if (fotoArchivo != null) {
                    // Obtengo una URI segura usando el FileProvider
                    fotoUri = FileProvider.getUriForFile(this,
                            "com.danimt.appsenderismo.fileprovider",
                            fotoArchivo);

                    // Lanzo la cámara pasándole la URI donde quiero que guarde la foto
                    camaraLauncher.launch(fotoUri);
                }
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear fichero de imagen", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para guardar la ruta
        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();

            // Validación nombre obligatorio
            if (nombre.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
            } else {
                String ubicacion = etLocalizacion.getText().toString();

                // Saco el texto del RadioButton seleccionado
                String tipo = "Circular";
                int selectedId = rgTipo.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRb = findViewById(selectedId);
                    tipo = selectedRb.getText().toString();
                }

                float dificultad = rbDificultad.getRating();
                double distancia = 0.0;

                // Uso try catch para evitar que la app pete si se meten letras o lo dejan vacío
                try {
                    distancia = Double.parseDouble(etDistancia.getText().toString());
                } catch (NumberFormatException e) { distancia = 0.0; }

                String descripcion = etDescripcion.getText().toString();
                boolean esFavorita = swFavorita.isChecked();

                // Coordenadas por defecto o las que escriba el usuario
                double lat = 37.99;
                double lon = -1.13;
                try {
                    lat = Double.parseDouble(etLat.getText().toString());
                    lon = Double.parseDouble(etLon.getText().toString());
                } catch (Exception e) {}

                // Si tengo ruta de foto la guardo, si no, guardo cadena vacía
                String uriGuardar = (rutaImg != null) ? rutaImg : "";

                // Creo el objeto con todos los datos
                Ruta nuevaRuta = new Ruta(nombre, ubicacion, tipo, dificultad, distancia, descripcion, esFavorita, lat, lon, uriGuardar);

                // Guardo en un hilo secundario para no bloquear la interfaz principal
                new Thread(() -> {
                    try {
                        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                        db.rutaDao().insert(nuevaRuta);

                        // Para mostrar el Toast y cerrar, tengo que volver al hilo principal
                        runOnUiThread(() -> {
                            Toast.makeText(AltaRutaActivity.this, getString(R.string.msg_saved), Toast.LENGTH_SHORT).show();
                            finish(); // Cierra la activity y vuelve atrás
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(AltaRutaActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }).start();
            }
        });
    }

    // Método auxiliar para generar nombres de archivo únicos con la fecha
    private File crearFicheroImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Crea el archivo temporal en la carpeta privada de la app
        File imagen = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Guardo la ruta absoluta para meterla luego en la BD
        rutaImg = "file://" + imagen.getAbsolutePath();
        return imagen;
    }
}