package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrarseActivity extends AppCompatActivity {

    private EditText editTextRegistrarUsuario, editTextRegistrarContraseña;
    private Button btnRegistrarUsuario, btnVolverRegistrarse;
    private CheckBox checkBoxMostrarContraseña;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        auth = FirebaseAuth.getInstance();

        editTextRegistrarUsuario = findViewById(R.id.editTextRegistrarUsuario);
        editTextRegistrarContraseña = findViewById(R.id.editTextRegistrarContraseña);
        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario);
        btnVolverRegistrarse = findViewById(R.id.btnVolverRegistrarse);
        checkBoxMostrarContraseña = findViewById(R.id.checkCoxMostrarContraseñaRegistrarse);

        checkBoxMostrarContraseña.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextRegistrarContraseña.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                editTextRegistrarContraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            editTextRegistrarContraseña.setSelection(editTextRegistrarContraseña.getText().length());
        });

        btnRegistrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextRegistrarUsuario.getText().toString().trim();
                String password = editTextRegistrarContraseña.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegistrarseActivity.this, "Por Favor, Ingrese Un Correo Electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegistrarseActivity.this, "Por Favor, Ingrese Una Contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(RegistrarseActivity.this, "La Contraseña Debe Tener Al Menos 6 Caracteres De Longitud", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegistrarseActivity.this, "Usuario Registrado Exitosamente", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistrarseActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegistrarseActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        btnVolverRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
