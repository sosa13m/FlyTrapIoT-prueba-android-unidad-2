package com.example.flytrapiot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Pantalla principal: listado de trampas (RecyclerView) y acceso para agregar nuevas.
 */
public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private TrampaAdapter adapter;
    private TextView tvVacio, tvResumen;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Ui.ajustarBordes(findViewById(R.id.root), findViewById(R.id.header));
        Ui.agregarMosca(this);

        dbHelper = new DBHelper(this);

        rv = findViewById(R.id.rvTrampas);
        tvVacio = findViewById(R.id.tvVacio);
        tvResumen = findViewById(R.id.tvResumen);
        Button btnAgregar = findViewById(R.id.btnAgregar);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TrampaAdapter(this, new ArrayList<>());
        rv.setAdapter(adapter);

        btnAgregar.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AgregarActivity.class)));
    }

    // Se recarga desde la BD cada vez que se vuelve a esta pantalla
    @Override
    protected void onResume() {
        super.onResume();
        cargarLista();
    }

    private void cargarLista() {
        ArrayList<Trampa> lista = dbHelper.obtenerTrampas();
        adapter.actualizarLista(lista);
        rv.scheduleLayoutAnimation(); // tarjetas entran deslizándose
        tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        int altas = 0;
        for (Trampa t : lista) {
            if (t.getNivelCaptura() >= Ui.UMBRAL_ALTO) altas++;
        }
        String resumen = lista.size() + (lista.size() == 1 ? " trampa registrada" : " trampas registradas");
        if (altas > 0) {
            resumen += "  ·  " + altas + " en nivel alto";
        }
        tvResumen.setText(resumen);
    }
}
