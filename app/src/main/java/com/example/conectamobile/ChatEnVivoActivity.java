package com.example.conectamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ChatEnVivoActivity extends AppCompatActivity {

    private ListView listViewMensajes;
    private EditText editTextMensaje;
    private Button btnEnviarMensaje, btnVolverChatEnVivo;
    private ArrayList<String> mensajesList;
    private ArrayAdapter<String> adapter;
    private HashSet<String> mensajesProcesados;
    private String topico;
    private String usuarioActual;
    private MqttAsyncClient mqttClient;
    private final String brokerUrl = "tcp://test.mosquitto.org:1883";
    private boolean isConnected = false;
    private boolean isConnecting = false;

    private DatabaseReference chatReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_en_vivo);

        listViewMensajes = findViewById(R.id.listViewMensajes);
        editTextMensaje = findViewById(R.id.editTextMensaje);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        btnVolverChatEnVivo = findViewById(R.id.btnVolverChatEnVivo);
        mensajesList = new ArrayList<>();
        mensajesProcesados = new HashSet<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mensajesList);
        listViewMensajes.setAdapter(adapter);

        topico = getIntent().getStringExtra("topico");
        usuarioActual = getIntent().getStringExtra("usuarioEmail");

        if (topico == null || usuarioActual == null || topico.isEmpty()) {
            Toast.makeText(this, "Error: Datos Faltantes Para Iniciar El Chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatReference = FirebaseDatabase.getInstance().getReference("Chats").child(topico).child("Mensajes");
        iniciarChat();
    }

    private void iniciarChat() {
        try {
            mqttClient = new MqttAsyncClient(brokerUrl, MqttAsyncClient.generateClientId(), null);
            conectarMQTT();
        } catch (MqttException e) {
            Log.e("ChatEnVivoActivity", "Error Al Inicializar MQTT: " + e.getMessage());
        }

        btnEnviarMensaje.setOnClickListener(v -> enviarMensaje());
        btnVolverChatEnVivo.setOnClickListener(v -> finish());
    }

    private void conectarMQTT() {
        try {
            if (mqttClient != null && (isConnected || isConnecting)) return;

            isConnecting = true;
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setKeepAliveInterval(60);

            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isConnected = true;
                    isConnecting = false;
                    suscribirTopico();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    isConnected = false;
                    isConnecting = false;
                    Log.e("ChatEnVivoActivity", "Error Al Conectar MQTT: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ChatEnVivoActivity", "Error al conectar MQTT: " + e.getMessage());
        }
    }

    private void suscribirTopico() {
        try {
            mqttClient.subscribe(topico, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("ChatEnVivoActivity", "Suscripción Exitosa Al Tópico");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("ChatEnVivoActivity", "Error Al Suscribirse Al Tópico: " + exception.getMessage());
                }
            });

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    isConnected = false;
                    Log.e("ChatEnVivoActivity", "Conexión MQTT Perdida: " + (cause != null ? cause.getMessage() : "desconocido"));
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String contenido = message.toString();

                    if (!mensajesProcesados.contains(contenido)) {
                        mensajesProcesados.add(contenido);
                        mensajesList.add(contenido);
                        runOnUiThread(() -> adapter.notifyDataSetChanged());

                        guardarMensajeEnFirebase(contenido);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });
        } catch (MqttException e) {
            Log.e("ChatEnVivoActivity", "Error Al Suscribirse Al Tópico");
        }
    }

    private void enviarMensaje() {
        String textoMensaje = editTextMensaje.getText().toString().trim();
        if (textoMensaje.isEmpty()) {
            Toast.makeText(this, "Ingrese Un Mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isConnected || mqttClient == null || !mqttClient.isConnected()) {
            Toast.makeText(this, "Reconectando A MQTT...", Toast.LENGTH_SHORT).show();
            conectarMQTT();
            return;
        }

        try {
            String nombreRemitente = extraerNombreDeCorreo(usuarioActual);
            String mensajeFormateado = nombreRemitente + ": " + textoMensaje;

            MqttMessage mqttMessage = new MqttMessage(mensajeFormateado.getBytes());
            mqttMessage.setQos(1);
            mqttClient.publish(topico, mqttMessage);

            mensajesProcesados.add(mensajeFormateado);
            mensajesList.add(mensajeFormateado);
            adapter.notifyDataSetChanged();

            guardarMensajeEnFirebase(mensajeFormateado);

            editTextMensaje.setText("");
        } catch (MqttException e) {
            Log.e("ChatEnVivoActivity", "Error Al Enviar Mensaje MQTT: " + e.getMessage());
        }
    }

    private void guardarMensajeEnFirebase(String mensaje) {
        String mensajeID = chatReference.push().getKey();
        if (mensajeID == null) return;

        Map<String, Object> mensajeData = new HashMap<>();
        mensajeData.put("texto", mensaje);
        mensajeData.put("timestamp", System.currentTimeMillis());

        chatReference.child(mensajeID).setValue(mensajeData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ChatEnVivoActivity", "Mensaje Guardado Exitosamente En Firebase.");
            } else {
                Log.e("ChatEnVivoActivity", "Error Al Guardar Mensaje En Firebase.");
            }
        });
    }

    private String extraerNombreDeCorreo(String correo) {
        int index = correo.indexOf("@");
        if (index != -1) {
            return correo.substring(0, index);
        }
        return correo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                mqttClient.close();
            }
        } catch (MqttException e) {
            Log.e("ChatEnVivoActivity", "Error Al Desconectar MQTT: " + e.getMessage());
        }
    }
}