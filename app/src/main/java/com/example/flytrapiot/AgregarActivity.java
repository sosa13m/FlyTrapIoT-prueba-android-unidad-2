package com.example.flytrapiot;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Formulario de creación (Crear). Inserta con ContentValues a través de DBHelper.
 */
public class AgregarActivity extends AppCompatActivity {

    private EditText etSector, etTipo, etNivel, etObs;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar);
        Ui.ajustarBordes(findViewById(R.id.root), findViewById(R.id.header));
        Ui.agregarMosca(this);
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        etSector = findViewById(R.id.etSector);
        etTipo = findViewById(R.id.etTipo);
        etNivel = findViewById(R.id.etNivel);
        etObs = findViewById(R.id.etObs);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void guardar() {
        String sector = etSector.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String nivelStr = etNivel.getText().toString().trim();
        String obs = etObs.getText().toString().trim();

        if (TextUtils.isEmpty(sector) || TextUtils.isEmpty(tipo) || TextUtils.isEmpty(nivelStr)) {
            Toast.makeText(this, "Sector, tipo y nivel de captura son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int nivel = Integer.parseInt(nivelStr);
        long id = dbHelper.insertarTrampa(sector, tipo, nivel, obs);

        if (id != -1) {
            Toast.makeText(this, "Trampa registrada (ID " + id + ")", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }
}
