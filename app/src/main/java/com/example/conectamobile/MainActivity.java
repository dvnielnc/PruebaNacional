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

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsuario, editTextContraseña;
    private Button btnIngresar, btnRegistrarse;
    private CheckBox checkBoxMostrarContraseña;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextContraseña = findViewById(R.id.editTextContraseña);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        checkBoxMostrarContraseña = findViewById(R.id.checkCoxMostrarContraseñaLogin);

        checkBoxMostrarContraseña.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextContraseña.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                editTextContraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            editTextContraseña.setSelection(editTextContraseña.getText().length());
        });

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextUsuario.getText().toString().trim();
                String password = editTextContraseña.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Por Favor, Ingrese Un Correo Electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Por Favor, Ingrese Una Contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Inicio De Sesión Exitoso", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(MainActivity.this, MenuPrincipalActivity.class);
                                intent.putExtra("usuarioEmail", email);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Usuario No Encontrado O Credenciales Incorrectas", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrarseActivity.class);
                startActivity(intent);
            }
        });
    }
}