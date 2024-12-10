package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class IniciarConversacionActivity extends AppCompatActivity {

    private ListView listViewIniciarConversacion;
    private Button btnVolverIniciarConversacion;
    private DatabaseReference databaseReference;
    private ArrayList<String> contactosList;
    private ArrayList<AgregarContactoActivity.Contacto> contactosObjList;
    private ArrayAdapter<String> adapter;
    private String usuarioEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_conversacion);

        listViewIniciarConversacion = findViewById(R.id.listViewIniciarConversacion);
        btnVolverIniciarConversacion = findViewById(R.id.btnVolverIniciarConversacion);

        usuarioEmail = getIntent().getStringExtra("usuarioEmail");

        if (usuarioEmail == null || usuarioEmail.isEmpty()) {
            Toast.makeText(this, "Error: Usuario No Identificado", Toast.LENGTH_SHORT).show();
            Log.e("IniciarConversacion", "Usuario No Identificado. Cerrando Actividad");
            finish();
            return;
        }

        Log.d("IniciarConversacion", "Usuario Actual: " + usuarioEmail);

        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(normalizarEmail(usuarioEmail)).child("Contactos");

        contactosList = new ArrayList<>();
        contactosObjList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactosList);

        listViewIniciarConversacion.setAdapter(adapter);

        cargarContactos();

        btnVolverIniciarConversacion.setOnClickListener(v -> {
            Intent intent = new Intent(IniciarConversacionActivity.this, MenuPrincipalActivity.class);
            intent.putExtra("usuarioEmail", usuarioEmail);
            startActivity(intent);
            finish();
        });

        listViewIniciarConversacion.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= contactosObjList.size()) {
                Toast.makeText(this, "Contacto No Válido Seleccionado", Toast.LENGTH_SHORT).show();
                Log.e("IniciarConversacion", "Posición Del Contacto Inválida: " + position);
                return;
            }

            AgregarContactoActivity.Contacto contactoSeleccionado = contactosObjList.get(position);

            if (contactoSeleccionado == null) {
                Toast.makeText(IniciarConversacionActivity.this, "Error: Contacto No Encontrado", Toast.LENGTH_SHORT).show();
                Log.e("IniciarConversacion", "Contacto Seleccionado Es Nulo.");
                return;
            }

            if (contactoSeleccionado.topico == null || contactoSeleccionado.topico.isEmpty() ||
                    contactoSeleccionado.nombre == null || contactoSeleccionado.nombre.isEmpty() ||
                    contactoSeleccionado.numero == null || contactoSeleccionado.numero.isEmpty()) {
                Toast.makeText(IniciarConversacionActivity.this, "Error: Datos Incompletos Del Contacto.", Toast.LENGTH_SHORT).show();
                Log.e("IniciarConversacion", "Datos Incompletos Del Contacto Seleccionado" +
                        "Tópico: " + contactoSeleccionado.topico +
                        ", Nombre: " + contactoSeleccionado.nombre +
                        ", Número: " + contactoSeleccionado.numero);
                return;
            }

            Log.d("IniciarConversacion", "Contacto Seleccionado: " + contactoSeleccionado.nombre +
                    ", Tópico: " + contactoSeleccionado.topico +
                    ", Número: " + contactoSeleccionado.numero);

            Intent intent = new Intent(IniciarConversacionActivity.this, ChatEnVivoActivity.class);
            intent.putExtra("usuarioEmail", usuarioEmail);
            intent.putExtra("topico", contactoSeleccionado.topico.trim());
            intent.putExtra("nombre", contactoSeleccionado.nombre.trim());
            intent.putExtra("numero", contactoSeleccionado.numero.trim());
            startActivity(intent);
        });
    }

    private void cargarContactos() {
        Log.d("IniciarConversacion", "Cargando Contactos Para: " + normalizarEmail(usuarioEmail));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactosList.clear();
                contactosObjList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AgregarContactoActivity.Contacto contacto = dataSnapshot.getValue(AgregarContactoActivity.Contacto.class);
                    if (contacto != null && contacto.topico != null && !contacto.topico.isEmpty()) {
                        contactosObjList.add(contacto);
                        String contactoInfo = "Tópico: " + contacto.topico + "\n"
                                + "Nombre: " + contacto.nombre + "\n"
                                + "Número: " + contacto.numero;
                        contactosList.add(contactoInfo);
                    } else {
                        Log.e("IniciarConversacion", "Contacto Inválido O Datos Incompletos En Firebase");
                    }
                }
                adapter.notifyDataSetChanged();

                if (contactosList.isEmpty()) {
                    Toast.makeText(IniciarConversacionActivity.this, "No Se Encontraron Contactos", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("IniciarConversacion", "Contactos Cargados: " + contactosList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IniciarConversacionActivity.this, "Error Al Cargar Contactos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("IniciarConversacion", "Error al cargar contactos: " + error.getMessage());
            }
        });
    }

    private String normalizarEmail(String email) {
        return email.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_");
    }
}