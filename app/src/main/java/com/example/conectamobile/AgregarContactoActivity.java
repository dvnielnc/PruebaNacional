package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AgregarContactoActivity extends AppCompatActivity {

    private EditText editTextTopico, editTextNombre, editTextNumero;
    private Button btnAgregarNuevoContacto, btnVolverAgregarContacto;
    private DatabaseReference databaseReference;
    private String usuarioEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        usuarioEmail = getIntent().getStringExtra("usuarioEmail");

        if (TextUtils.isEmpty(usuarioEmail)) {
            Toast.makeText(this, "Error: Usuario No Identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String usuarioNormalizado = normalizarEmail(usuarioEmail);
        Log.d("AgregarContactoActivity", "Usuario Normalizado: " + usuarioNormalizado);

        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(usuarioNormalizado).child("Contactos");

        editTextTopico = findViewById(R.id.editTextTopico);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextNumero = findViewById(R.id.editTextNumero);
        btnAgregarNuevoContacto = findViewById(R.id.btnAgregarNuevoContacto);
        btnVolverAgregarContacto = findViewById(R.id.btnVolverAgregarContacto);

        btnAgregarNuevoContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topico = editTextTopico.getText().toString().trim();
                String nombre = editTextNombre.getText().toString().trim();
                String numero = editTextNumero.getText().toString().trim();

                if (TextUtils.isEmpty(topico) || TextUtils.isEmpty(nombre) || TextUtils.isEmpty(numero)) {
                    Toast.makeText(AgregarContactoActivity.this, "Por Favor, Complete Todos Los Campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                String id = databaseReference.push().getKey();
                Contacto contacto = new Contacto(id, topico, nombre, numero);
                databaseReference.child(id).setValue(contacto).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AgregarContactoActivity.this, "Contacto Agregado Exitosamente", Toast.LENGTH_SHORT).show();
                        editTextTopico.setText("");
                        editTextNombre.setText("");
                        editTextNumero.setText("");
                    } else {
                        Toast.makeText(AgregarContactoActivity.this, "Error Al Guardar El Contacto", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnVolverAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgregarContactoActivity.this, MisContactosActivity.class);
                intent.putExtra("usuarioEmail", usuarioEmail);
                startActivity(intent);
                finish();
            }
        });
    }

    private String normalizarEmail(String email) {
        return email.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_");
    }

    public static class Contacto {
        public String id;
        public String topico;
        public String nombre;
        public String numero;

        public Contacto() {
        }

        public Contacto(String id, String topico, String nombre, String numero) {
            this.id = id;
            this.topico = topico;
            this.nombre = nombre;
            this.numero = numero;
        }
    }
}