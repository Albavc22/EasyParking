package com.example.easyparking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyparking.Usuarios.AdapterParking;
import com.example.easyparking.Usuarios.Parking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParkingActivity extends AppCompatActivity {

    RecyclerView rv;

    List<Parking> parkings;

    AdapterParking adapter;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        setToolbar(); //Setear Toolbar como action bar

        rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        parkings = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("parkings");
        user = FirebaseAuth.getInstance().getCurrentUser();

        adapter = new AdapterParking(parkings);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ParkingActivity.this, DatosParkingActivity.class);
                i.putExtra("Nombre", parkings.get(rv.getChildPosition(v)).getNombre());
                i.putExtra("Calle", parkings.get(rv.getChildPosition(v)).getCalle());
                i.putExtra("Precio", parkings.get(rv.getChildPosition(v)).getPrecio());
                i.putExtra("Horario", parkings.get(rv.getChildPosition(v)).getHorario());
                startActivity(i);
            }
        });
        rv.setAdapter(adapter);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parkings.removeAll(parkings);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Parking parking = snapshot.getValue(Parking.class);
                    parkings.add(parking);
                }
                adapter.notifyDataSetChanged();
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
