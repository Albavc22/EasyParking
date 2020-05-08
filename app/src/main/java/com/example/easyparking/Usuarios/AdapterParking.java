package com.example.easyparking.Usuarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyparking.R;

import java.util.List;

public class AdapterParking extends RecyclerView.Adapter<AdapterParking.ParkingViewHolder> implements View.OnClickListener{

    List<Parking> parkings;
    private View.OnClickListener listener;

    public AdapterParking(List<Parking> parkings) {
        this.parkings = parkings;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recycler, parent, false);
        v.setOnClickListener(this);
        ParkingViewHolder holder = new ParkingViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        Parking parking = parkings.get(position);
        holder.textViewNombre.setText(parking.getNombre());
        holder.textViewCalle.setText(parking.getCalle());
    }

    @Override
    public int getItemCount() {
        return parkings.size();
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

    public static class ParkingViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNombre, textViewCalle;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCalle = itemView.findViewById(R.id.modelo);
            textViewNombre = itemView.findViewById(R.id.matricula);
        }
    }
}
