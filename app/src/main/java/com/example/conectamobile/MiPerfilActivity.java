package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MiPerfilActivity extends AppCompatActivity {

    private Button btnVolverMiPerfil, btnNombrePerfil, btnUrlImagen;
    private EditText editTextNombrePerfil, editTextUrlImagen;
    private TextView textViewNombrePerfil;
    private ImageView imageViewPerfil;
    private DatabaseReference referenciaFirebase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        btnVolverMiPerfil = findViewById(R.id.btnVolverMiPerfil);
        btnNombrePerfil = findViewById(R.id.btnNombrePerfil);
        btnUrlImagen = findViewById(R.id.btnUrlImagen);
        editTextNombrePerfil = findViewById(R.id.editTextNombrePerfil);
        editTextUrlImagen = findViewById(R.id.editTextUrlImagen);
        textViewNombrePerfil = findViewById(R.id.textViewNombrePerfil);
        imageViewPerfil = findViewById(R.id.imageViewPerfil);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        referenciaFirebase = FirebaseDatabase.getInstance().getReference("Usuarios").child(userId);

        cargarDatosDesdeFirebase();

        btnVolverMiPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MiPerfilActivity.this, MenuPrincipalActivity.class);
            startActivity(intent);
            finish();
        });

        btnNombrePerfil.setOnClickListener(v -> {
            String nombre = editTextNombrePerfil.getText().toString().trim();
            if (!TextUtils.isEmpty(nombre)) {
                textViewNombrePerfil.setText("Nombre: " + nombre);

                referenciaFirebase.child("nombre").setValue(nombre)
                        .addOnSuccessListener(aVoid -> Toast.makeText(MiPerfilActivity.this, "Nombre Guardado Correctamente", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(MiPerfilActivity.this, "Error Al Guardar El Nombre", Toast.LENGTH_SHORT).show());

                editTextNombrePerfil.setText("");
            } else {
                Toast.makeText(MiPerfilActivity.this, "Por Favor Ingresa Un Nombre", Toast.LENGTH_SHORT).show();
            }
        });

        btnUrlImagen.setOnClickListener(v -> {
            String url = editTextUrlImagen.getText().toString().trim();
            if (!TextUtils.isEmpty(url)) {
                Glide.with(MiPerfilActivity.this)
                        .load(url)
                        .into(imageViewPerfil);

                referenciaFirebase.child("urlImagen").setValue(url)
                        .addOnSuccessListener(aVoid -> Toast.makeText(MiPerfilActivity.this, "Imagen Guardada Correctamente", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(MiPerfilActivity.this, "Error Al Guardar La Imagen", Toast.LENGTH_SHORT).show());

                editTextUrlImagen.setText("");
            } else {
                Toast.makeText(MiPerfilActivity.this, "Por Favor Ingresa Una URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatosDesdeFirebase() {
        referenciaFirebase.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String nombre = snapshot.child("nombre").getValue(String.class);
                String urlImagen = snapshot.child("urlImagen").getValue(String.class);

                if (nombre != null) {
                    textViewNombrePerfil.setText("Nombre: " + nombre);
                }

                if (urlImagen != null) {
                    Glide.with(MiPerfilActivity.this)
                            .load(urlImagen)
                            .into(imageViewPerfil);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(MiPerfilActivity.this, "Error Al Cargar Datos", Toast.LENGTH_SHORT).show());
    }
}
