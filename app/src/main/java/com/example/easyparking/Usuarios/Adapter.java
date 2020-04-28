package com.example.easyparking.Usuarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyparking.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.VehiculosviewHolder> implements View.OnClickListener {

    List<Vehiculo> vehiculos;
    private View.OnClickListener listener;

    public Adapter(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    @NonNull
    @Override
    public VehiculosviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recycler, parent, false);
        itemView.setOnClickListener(this);
        VehiculosviewHolder holder = new VehiculosviewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculosviewHolder holder, int position) {
        Vehiculo vehiculo = vehiculos.get(position);
        holder.textViewMatricula.setText(vehiculo.getMatricula());
        holder.textViewModelo.setText(vehiculo.getModelo());
    }

    @Override
    public int getItemCount() {
        return vehiculos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public static class VehiculosviewHolder extends RecyclerView.ViewHolder {

        TextView textViewMatricula, textViewModelo;

        public VehiculosviewHolder(@NonNull View itemView) {
            super(itemView);
            textViewModelo = itemView.findViewById(R.id.modelo);
            textViewMatricula = itemView.findViewById(R.id.matricula);
        }
    }

}
