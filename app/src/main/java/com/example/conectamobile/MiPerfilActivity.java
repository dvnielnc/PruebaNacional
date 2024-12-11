package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MiPerfilActivity extends AppCompatActivity {

    private Button btnVolverMiPerfil, btnNombrePerfil, btnUrlImagen;
    private EditText editTextNombrePerfil, editTextUrlImagen;
    private TextView textViewNombrePerfil;
    private ImageView imageViewPerfil;
    private DatabaseReference referenciaFirebase;

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

        referenciaFirebase = FirebaseDatabase.getInstance().getReference("Perfiles");

        cargarDatosDesdeFirebase();

        btnVolverMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MiPerfilActivity.this, MenuPrincipalActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnNombrePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombrePerfil.getText().toString().trim();
                if (!nombre.isEmpty()) {
                    textViewNombrePerfil.setText("Nombre: " + nombre);

                    referenciaFirebase.child("nombre").setValue(nombre).addOnSuccessListener(aVoid ->
                            Toast.makeText(MiPerfilActivity.this, "Nombre Guardado Correctamente", Toast.LENGTH_SHORT).show()
                    ).addOnFailureListener(e ->
                            Toast.makeText(MiPerfilActivity.this, "Error Al Guardar El Nombre", Toast.LENGTH_SHORT).show()
                    );

                    editTextNombrePerfil.setText("");
                } else {
                    Toast.makeText(MiPerfilActivity.this, "Por Favor Ingresa Un Nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnUrlImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editTextUrlImagen.getText().toString().trim();
                if (!url.isEmpty()) {
                    Glide.with(MiPerfilActivity.this)
                            .load(url)
                            .into(imageViewPerfil);

                    referenciaFirebase.child("urlImagen").setValue(url).addOnSuccessListener(aVoid ->
                            Toast.makeText(MiPerfilActivity.this, "Imagen Guardada Correctamente", Toast.LENGTH_SHORT).show()
                    ).addOnFailureListener(e ->
                            Toast.makeText(MiPerfilActivity.this, "Error Al Guardar La Imagen", Toast.LENGTH_SHORT).show()
                    );

                    editTextUrlImagen.setText("");
                } else {
                    Toast.makeText(MiPerfilActivity.this, "Por Favor Ingresa Una URL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarDatosDesdeFirebase() {
        referenciaFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("nombre")) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    textViewNombrePerfil.setText("Nombre: " + nombre);
                }

                if (snapshot.hasChild("urlImagen")) {
                    String urlImagen = snapshot.child("urlImagen").getValue(String.class);
                    Glide.with(MiPerfilActivity.this)
                            .load(urlImagen)
                            .into(imageViewPerfil);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MiPerfilActivity.this, "Error Al Cargar Datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
