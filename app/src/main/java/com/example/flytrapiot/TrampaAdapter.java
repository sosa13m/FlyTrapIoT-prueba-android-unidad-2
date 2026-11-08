package com.example.flytrapiot;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Adapter del RecyclerView. Al presionar un ítem captura su ID
 * y lo envía a DetalleActivity mediante Intent.putExtra.
 */
public class TrampaAdapter extends RecyclerView.Adapter<TrampaAdapter.TrampaViewHolder> {

    private final Context context;
    private ArrayList<Trampa> lista;

    public TrampaAdapter(Context context, ArrayList<Trampa> lista) {
        this.context = context;
        this.lista = lista;
    }

    public void actualizarLista(ArrayList<Trampa> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrampaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_trampa, parent, false);
        return new TrampaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrampaViewHolder holder, int position) {
        Trampa t = lista.get(position);
        int color = ContextCompat.getColor(context, Ui.colorNivel(t.getNivelCaptura()));

        holder.tvId.setText("TRAMPA #" + t.getId());
        holder.tvSector.setText(t.getCodigoSector().toUpperCase());
        holder.tvTipo.setText(t.getTipoTrampa());
        holder.tvNivel.setText(String.valueOf(t.getNivelCaptura()));
        holder.tvEstado.setText(Ui.textoNivel(t.getNivelCaptura()));

        holder.vFranja.setBackgroundColor(color);
        holder.boxNivel.setBackgroundTintList(ColorStateList.valueOf(color));

        if (TextUtils.isEmpty(t.getObservaciones())) {
            holder.tvObs.setVisibility(View.GONE);
        } else {
            holder.tvObs.setVisibility(View.VISIBLE);
            holder.tvObs.setText(t.getObservaciones());
        }

        // Captura del ID y envío vía Intent con putExtra
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleActivity.class);
            intent.putExtra("id_trampa", t.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class TrampaViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvSector, tvTipo, tvObs, tvNivel, tvEstado;
        View vFranja;
        LinearLayout boxNivel;

        TrampaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvSector = itemView.findViewById(R.id.tvSector);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvObs = itemView.findViewById(R.id.tvObs);
            tvNivel = itemView.findViewById(R.id.tvNivel);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            vFranja = itemView.findViewById(R.id.vFranja);
            boxNivel = itemView.findViewById(R.id.boxNivel);
        }
    }
}
