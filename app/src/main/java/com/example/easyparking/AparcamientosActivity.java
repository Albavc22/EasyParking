package com.example.easyparking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyparking.Usuarios.AdapterEstacionamientos;
import com.example.easyparking.Usuarios.Aparcamiento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AparcamientosActivity extends AppCompatActivity {

    RecyclerView rv;

    List<Aparcamiento> aparcamientos;

    AdapterEstacionamientos adapterEstacionamientos;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    TextView textViewInfo1, textViewInfo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aparcamientos);

        setToolbar(); //Setear Toolbar como action bar

        textViewInfo1 = findViewById(R.id.info1_estacionamientos);
        textViewInfo2 = findViewById(R.id.info2_estacionamientos);

        textViewInfo1.setText("Todavía no has iniciado ningún estacionamiento");
        textViewInfo2.setText("Inicia un estacionamiento para comenzar");

        rv = findViewById(R.id.recycler_estacionamientos);
        rv.setLayoutManager(new LinearLayoutManager(this));

        aparcamientos = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        user = FirebaseAuth.getInstance().getCurrentUser();

        adapterEstacionamientos = new AdapterEstacionamientos(aparcamientos);
        adapterEstacionamientos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tiempo = aparcamientos.get(rv.getChildPosition(v)).getFecha().split(" ");
                Intent i = new Intent(AparcamientosActivity.this, AparcamientoRegistradoActivity.class);
                i.putExtra("Matricula", aparcamientos.get(rv.getChildPosition(v)).getVehiculo().getMatricula());
                i.putExtra("Modelo", aparcamientos.get(rv.getChildPosition(v)).getVehiculo().getModelo());
                i.putExtra("Fecha", tiempo[0]);
                i.putExtra("Hora", tiempo[1]);
                i.putExtra("Calle", aparcamientos.get(rv.getChildPosition(v)).getCalle());
                i.putExtra("Zona", aparcamientos.get(rv.getChildPosition(v)).getZona());
                startActivity(i);
            }
        });

        rv.setAdapter(adapterEstacionamientos);

        myRef.child(user.getUid()).child("aparcamientos en curso").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                aparcamientos.removeAll(aparcamientos);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Aparcamiento aparcamiento = snapshot.getValue(Aparcamiento.class);
                    if (aparcamiento.getAparcado()) { //Si el coche esta aparcado
                        aparcamientos.add(aparcamiento);
                    }
                }
                adapterEstacionamientos.notifyDataSetChanged();
                if (!aparcamientos.isEmpty()) {
                    textViewInfo1.setText("");
                    textViewInfo2.setText("");
                } else {
                    textViewInfo1.setText("Todavía no has iniciado ningún estacionamiento");
                    textViewInfo2.setText("Inicia un estacionamiento para comenzar");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }


}
