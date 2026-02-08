package com.danimt.appsenderismo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AcercaDeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);

        Button btnSoporte = findViewById(R.id.btnSoporte);

        btnSoporte.setOnClickListener(v -> {
            // Intent implícito para que el sistema abra la app de correo
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // Filtro para que solo salgan apps de email
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"soporte@senderismoapp.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));

            // Compruebo que el usuario tiene app de correo para evitar que crashee
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });
    }
}