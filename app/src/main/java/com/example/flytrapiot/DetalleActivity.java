package com.example.flytrapiot;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Detalle / edición. Recibe el ID vía Intent, recupera el registro desde SQLite,
 * lo muestra en el formulario y permite UPDATE y DELETE (sin diálogo de confirmación).
 */
public class DetalleActivity extends AppCompatActivity {

    private EditText etSector, etTipo, etNivel, etObs;
    private DBHelper dbHelper;
    private int idTrampa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);
        Ui.ajustarBordes(findViewById(R.id.root), findViewById(R.id.header));
        Ui.agregarMosca(this);
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        TextView tvIdDetalle = findViewById(R.id.tvIdDetalle);
        etSector = findViewById(R.id.etSector);
        etTipo = findViewById(R.id.etTipo);
        etNivel = findViewById(R.id.etNivel);
        etObs = findViewById(R.id.etObs);
        Button btnActualizar = findViewById(R.id.btnActualizar);
        Button btnEliminar = findViewById(R.id.btnEliminar);

        // Recepción de la clave única vía Intent
        idTrampa = getIntent().getIntExtra("id_trampa", -1);
        if (idTrampa == -1) {
            Toast.makeText(this, "ID no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Recuperación del registro desde SQLite
        Trampa t = dbHelper.obtenerTrampaPorId(idTrampa);
        if (t == null) {
            Toast.makeText(this, "Registro no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Despliegue en el formulario
        tvIdDetalle.setText("Trampa #" + t.getId());
        etSector.setText(t.getCodigoSector());
        etTipo.setText(t.getTipoTrampa());
        etNivel.setText(String.valueOf(t.getNivelCaptura()));
        etObs.setText(t.getObservaciones());

        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());
    }

    private void actualizar() {
        String sector = etSector.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();
        String nivelStr = etNivel.getText().toString().trim();
        String obs = etObs.getText().toString().trim();

        if (TextUtils.isEmpty(sector) || TextUtils.isEmpty(tipo) || TextUtils.isEmpty(nivelStr)) {
            Toast.makeText(this, "Sector, tipo y nivel de captura son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int filas = dbHelper.actualizarTrampa(idTrampa, sector, tipo, Integer.parseInt(nivelStr), obs);
        Toast.makeText(this, filas > 0 ? "Trampa actualizada" : "No se pudo actualizar",
                Toast.LENGTH_SHORT).show();
        if (filas > 0) finish();
    }

    // Eliminación directa e inmediata, sin Dialog de confirmación (requisito)
    private void eliminar() {
        int filas = dbHelper.eliminarTrampa(idTrampa);
        Toast.makeText(this, filas > 0 ? "Trampa eliminada" : "No se pudo eliminar",
                Toast.LENGTH_SHORT).show();
        finish();
    }
}
