package com.example.easyparking;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyparking.Usuarios.Adapter;
import com.example.easyparking.Usuarios.Vehiculo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VehiculoActivity extends AppCompatActivity {

    RecyclerView rv;

    List<Vehiculo> vehiculos;

    Adapter adapter;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    TextView textViewInfo1, textViewInfo2, textViewTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);

        setToolbar(); //Setear Toolbar como action bar

        textViewInfo1 = findViewById(R.id.info1);
        textViewInfo2 = findViewById(R.id.info2);
        textViewTitulo = findViewById(R.id.textView_datos);

        textViewInfo1.setText(R.string.no_vehiculos_texto1);
        textViewInfo2.setText(R.string.no_vehiculos_texto2);

        rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        vehiculos = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        user = FirebaseAuth.getInstance().getCurrentUser();

        adapter = new Adapter(vehiculos);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VehiculoActivity.this, VehiculoRegistradoActivity.class);
                i.putExtra("Matricula", vehiculos.get(rv.getChildPosition(v)).getMatricula());
                i.putExtra("Modelo", vehiculos.get(rv.getChildPosition(v)).getModelo());
                startActivity(i);
            }
        });
        rv.setAdapter(adapter);

        myRef.child(user.getUid()).child("vehiculos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehiculos.removeAll(vehiculos);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vehiculo vehiculo = snapshot.getValue(Vehiculo.class);
                    vehiculos.add(vehiculo);
                }
                adapter.notifyDataSetChanged();
                if (dataSnapshot.exists()) {
                    textViewTitulo.setText(R.string.vehiculos_registrados);
                    textViewInfo1.setText("");
                    textViewInfo2.setText("");
                } else {
                    textViewInfo1.setText(R.string.no_vehiculos_texto1);
                    textViewInfo2.setText(R.string.no_vehiculos_texto2);
                    textViewTitulo.setText("");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vehiculo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                startActivity(new Intent(VehiculoActivity.this, AddVehiculoActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
