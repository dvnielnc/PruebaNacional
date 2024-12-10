package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditarContactosActivity extends AppCompatActivity {

    private EditText editTextNuevoTopico, editTextNuevoNombre, editTextNuevoNumero;
    private Button btnGuardarCambios, btnEliminarContacto, btnVolverEditarContacto;
    private DatabaseReference databaseReference;
    private String contactoId, usuarioEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contactos);

        contactoId = getIntent().getStringExtra("contactoId");
        usuarioEmail = getIntent().getStringExtra("usuarioEmail");
        String topico = getIntent().getStringExtra("topico");
        String nombre = getIntent().getStringExtra("nombre");
        String numero = getIntent().getStringExtra("numero");

        if (TextUtils.isEmpty(contactoId) || TextUtils.isEmpty(usuarioEmail)) {
            Toast.makeText(this, "Error: Datos Faltantes Para Editar El Contacto.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String usuarioPath = usuarioEmail.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_");
        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios").child(usuarioPath).child("Contactos");

        editTextNuevoTopico = findViewById(R.id.editTextNuevoTopico);
        editTextNuevoNombre = findViewById(R.id.editTextNuevoNombre);
        editTextNuevoNumero = findViewById(R.id.editTextNuevoNumero);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnEliminarContacto = findViewById(R.id.btnEliminarContacto);
        btnVolverEditarContacto = findViewById(R.id.btnVolverEditarContacto);

        editTextNuevoTopico.setText(topico);
        editTextNuevoNombre.setText(nombre);
        editTextNuevoNumero.setText(numero);

        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoTopico = editTextNuevoTopico.getText().toString().trim();
                String nuevoNombre = editTextNuevoNombre.getText().toString().trim();
                String nuevoNumero = editTextNuevoNumero.getText().toString().trim();

                if (TextUtils.isEmpty(nuevoTopico) || TextUtils.isEmpty(nuevoNombre) || TextUtils.isEmpty(nuevoNumero)) {
                    Toast.makeText(EditarContactosActivity.this, "Por Favor, Completa Todos Los Campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference contactoRef = databaseReference.child(contactoId);
                contactoRef.child("topico").setValue(nuevoTopico);
                contactoRef.child("nombre").setValue(nuevoNombre);
                contactoRef.child("numero").setValue(nuevoNumero);

                Toast.makeText(EditarContactosActivity.this, "Cambios Guardados Correctamente", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(EditarContactosActivity.this, MisContactosActivity.class);
                intent.putExtra("usuarioEmail", usuarioEmail);
                startActivity(intent);
                finish();
            }
        });

        btnEliminarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(contactoId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditarContactosActivity.this, "Contacto Eliminado Correctamente", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EditarContactosActivity.this, MisContactosActivity.class);
                        intent.putExtra("usuarioEmail", usuarioEmail);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(EditarContactosActivity.this, "Error Al Eliminar El Contacto", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnVolverEditarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarContactosActivity.this, MisContactosActivity.class);
                intent.putExtra("usuarioEmail", usuarioEmail);
                startActivity(intent);
                finish();
            }
        });
    }
}