package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MiPerfilActivity extends AppCompatActivity {

    private Button btnVolverMiPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        btnVolverMiPerfil = findViewById(R.id.btnVolverMiPerfil);
        btnVolverMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MiPerfilActivity.this, MenuPrincipalActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}