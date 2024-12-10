package com.example.conectamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

public class MisContactosActivity extends AppCompatActivity {

    private Button btnAgregarContacto, btnVolverMisContactos;
    private ListView listViewMostrarContactos;
    private DatabaseReference databaseReference;
    private ArrayList<String> contactosList;
    private ArrayAdapter<String> adapter;
    private HashMap<String, AgregarContactoActivity.Contacto> contactosMap;
    private String usuarioEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_contactos);

        btnAgregarContacto = findViewById(R.id.btnAgregarContacto);
        btnVolverMisContactos = findViewById(R.id.btnVolverMisContactos);
        listViewMostrarContactos = findViewById(R.id.listViewMostrarContactos);

        usuarioEmail = getIntent().getStringExtra("usuarioEmail");

        if (usuarioEmail == null || usuarioEmail.isEmpty()) {
            Toast.makeText(this, "Error: Usuario No Identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("MisContactosActivity", "Usuario actual: " + usuarioEmail);

        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios")
                .child(normalizarEmail(usuarioEmail)).child("Contactos");

        contactosList = new ArrayList<>();
        contactosMap = new HashMap<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactosList);
        listViewMostrarContactos.setAdapter(adapter);

        btnAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MisContactosActivity.this, AgregarContactoActivity.class);
                intent.putExtra("usuarioEmail", usuarioEmail);
                startActivity(intent);
            }
        });

        btnVolverMisContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MisContactosActivity.this, MenuPrincipalActivity.class);
                intent.putExtra("usuarioEmail", usuarioEmail);
                startActivity(intent);
                finish();
            }
        });

        cargarContactos();

        listViewMostrarContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = contactosList.get(position);
                AgregarContactoActivity.Contacto contactoSeleccionado = null;

                for (AgregarContactoActivity.Contacto contacto : contactosMap.values()) {
                    String formattedContacto = "Tópico: " + contacto.topico + "\n"
                            + "Nombre: " + contacto.nombre + "\n"
                            + "Número: " + contacto.numero;
                    if (formattedContacto.equals(selectedItem)) {
                        contactoSeleccionado = contacto;
                        break;
                    }
                }

                if (contactoSeleccionado != null) {
                    Intent intent = new Intent(MisContactosActivity.this, EditarContactosActivity.class);
                    intent.putExtra("usuarioEmail", usuarioEmail);
                    intent.putExtra("contactoId", contactoSeleccionado.id);
                    intent.putExtra("topico", contactoSeleccionado.topico);
                    intent.putExtra("nombre", contactoSeleccionado.nombre);
                    intent.putExtra("numero", contactoSeleccionado.numero);
                    startActivity(intent);
                }
            }
        });
    }

    private void cargarContactos() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contactosList.clear();
                contactosMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AgregarContactoActivity.Contacto contacto = snapshot.getValue(AgregarContactoActivity.Contacto.class);
                    if (contacto != null) {
                        String contactoInfo = "Tópico: " + contacto.topico + "\n"
                                + "Nombre: " + contacto.nombre + "\n"
                                + "Número: " + contacto.numero;
                        contactosList.add(contactoInfo);
                        contactosMap.put(contacto.id, contacto);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error Al Cargar Los Contactos: " + databaseError.getMessage());
                Toast.makeText(MisContactosActivity.this, "Error Al Cargar Los Contactos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String normalizarEmail(String email) {
        return email.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_");
    }
}
