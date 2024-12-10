package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MenuPrincipalActivity extends AppCompatActivity {

    private Button btnCerrarSesion, btnMisContactos, btnIniciarConversacion, btnMiPerfil;
    private FirebaseAuth auth;
    private String usuarioEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        auth = FirebaseAuth.getInstance();

        usuarioEmail = getIntent().getStringExtra("usuarioEmail");

        if (usuarioEmail == null || usuarioEmail.isEmpty()) {
            Toast.makeText(this, "Error: Usuario No Identificado", Toast.LENGTH_SHORT).show();
            Log.e("MenuPrincipalActivity", "Usuario No Identificado. Cerrando Actividad.");
            finish();
            return;
        }

        Log.d("MenuPrincipalActivity", "Usuario Actual: " + usuarioEmail);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();

                Toast.makeText(MenuPrincipalActivity.this, "Cerrando Sesión...", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MenuPrincipalActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnMisContactos = findViewById(R.id.btnMisContactos);
        btnMisContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usuarioEmail == null || usuarioEmail.isEmpty()) {
                    Toast.makeText(MenuPrincipalActivity.this, "Error: Usuario No Identificado", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("MenuPrincipalActivity", "Navegando a MisContactosActivity con Usuario: " + usuarioEmail);

                Intent intent = new Intent(MenuPrincipalActivity.this, MisContactosActivity.class);
                intent.putExtra("usuarioEmail", usuarioEmail);
                startActivity(intent);
            }
        });

        btnIniciarConversacion = findViewById(R.id.btnIniciarConversacion);
        btnIniciarConversacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usuarioEmail == null || usuarioEmail.isEmpty()) {
                    Toast.makeText(MenuPrincipalActivity.this, "Error: Usuario No Identificado", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("MenuPrincipalActivity", "Navegando a IniciarConversacionActivity Con Usuario: " + usuarioEmail);

                Intent intent = new Intent(MenuPrincipalActivity.this, IniciarConversacionActivity.class);
                intent.putExtra("usuarioEmail", usuarioEmail);
                startActivity(intent);
            }
        });

        btnMiPerfil = findViewById(R.id.btnMiPerfil);
        btnMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usuarioEmail == null || usuarioEmail.isEmpty()) {
                    Toast.makeText(MenuPrincipalActivity.this, "Error: Usuario No Identificado.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("MenuPrincipalActivity", "Navegando a MiPerfilActivity Con Usuario: " + usuarioEmail);

                Intent intent = new Intent(MenuPrincipalActivity.this, MiPerfilActivity.class);
                intent.putExtra("usuarioEmail", usuarioEmail);
                startActivity(intent);
            }
        });
    }
}