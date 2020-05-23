package com.example.easyparking.Usuarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyparking.R;

import java.util.List;

public class AdapterEstacionamientos extends RecyclerView.Adapter<AdapterEstacionamientos.AparcamientosViewHolder> implements View.OnClickListener {

    List<Aparcamiento> aparcamientoList;
    private View.OnClickListener listener;

    public AdapterEstacionamientos(List<Aparcamiento> aparcamientoList) {
        this.aparcamientoList = aparcamientoList;
    }

    @NonNull
    @Override
    public AparcamientosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_aparcamientos, parent, false);
        v.setOnClickListener(this);
        AparcamientosViewHolder holder = new AparcamientosViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AparcamientosViewHolder holder, int position) {
        Aparcamiento aparcamiento = aparcamientoList.get(position);
        String[] tiempo = aparcamiento.getFecha().split(" ");
        holder.matricula.setText(aparcamiento.getVehiculo().getMatricula());
        holder.modelo.append(": " +aparcamiento.getVehiculo().getModelo());
        holder.fecha.append(": " +tiempo[0]);
        holder.hora.append(": " +tiempo[1]);
        holder.calle.append(": " +aparcamiento.getCalle());
        holder.zona.append(": " +aparcamiento.getZona());
    }

    @Override
    public int getItemCount() {
        return aparcamientoList.size();
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

    public static class AparcamientosViewHolder extends RecyclerView.ViewHolder {

        TextView matricula, modelo, fecha, hora, calle, zona;

        public AparcamientosViewHolder(@NonNull View itemView) {
            super(itemView);
            matricula = itemView.findViewById(R.id.matricula_aparcamiento_row);
            modelo = itemView.findViewById(R.id.modelo_aparcamiento_row);
            fecha = itemView.findViewById(R.id.fecha_aparcamiento_row);
            hora = itemView.findViewById(R.id.hora_aparcamiento_row);
            calle = itemView.findViewById(R.id.calle_aparcamiento_row);
            zona = itemView.findViewById(R.id.zona_aparcamiento_row);
        }
    }
}
