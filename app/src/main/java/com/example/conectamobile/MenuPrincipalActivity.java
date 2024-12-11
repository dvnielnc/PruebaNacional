package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPrincipalActivity extends AppCompatActivity {

    private Button btnCerrarSesion, btnMisContactos, btnIniciarConversacion, btnMiPerfil;
    private FirebaseAuth auth;
    private String usuarioEmail;
    private DatabaseReference referenciaFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        auth = FirebaseAuth.getInstance();
        referenciaFirebase = FirebaseDatabase.getInstance().getReference("usuarios");

        usuarioEmail = getIntent().getStringExtra("usuarioEmail");

        if (usuarioEmail == null || usuarioEmail.isEmpty()) {
            if (auth.getCurrentUser() != null) {
                usuarioEmail = auth.getCurrentUser().getEmail();
            }
        }

        if (usuarioEmail == null || usuarioEmail.isEmpty()) {
            Toast.makeText(this, "Error: Usuario No Identificado", Toast.LENGTH_SHORT).show();
            Log.e("MenuPrincipalActivity", "Usuario No Identificado. Cerrando Actividad.");
            finish();
            return;
        }

        Log.d("MenuPrincipalActivity", "Usuario Actual: " + usuarioEmail);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnMisContactos = findViewById(R.id.btnMisContactos);
        btnIniciarConversacion = findViewById(R.id.btnIniciarConversacion);
        btnMiPerfil = findViewById(R.id.btnMiPerfil);

        configurarBotones();
    }

    private void configurarBotones() {
        // Botón para cerrar sesión
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

        btnMisContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navegarAActividad(MisContactosActivity.class);
            }
        });

        btnIniciarConversacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navegarAActividad(IniciarConversacionActivity.class);
            }
        });

        btnMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navegarAActividad(MiPerfilActivity.class);
            }
        });
    }

    private void navegarAActividad(Class<?> actividadDestino) {
        if (usuarioEmail == null || usuarioEmail.isEmpty()) {
            Toast.makeText(this, "Error: Usuario No Identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("MenuPrincipalActivity", "Navegando a " + actividadDestino.getSimpleName() + " con Usuario: " + usuarioEmail);

        Intent intent = new Intent(MenuPrincipalActivity.this, actividadDestino);
        intent.putExtra("usuarioEmail", usuarioEmail);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recuperarDatosUsuario();
    }

    private void recuperarDatosUsuario() {
        referenciaFirebase.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("MenuPrincipalActivity", "Datos de usuario cargados correctamente.");
                } else {
                    Log.e("MenuPrincipalActivity", "No se encontraron datos para el usuario.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MenuPrincipalActivity", "Error al recuperar datos: " + error.getMessage());
            }
        });
    }
}