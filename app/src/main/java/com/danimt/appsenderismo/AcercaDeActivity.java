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
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"soporte@senderismoapp.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });
    }
}