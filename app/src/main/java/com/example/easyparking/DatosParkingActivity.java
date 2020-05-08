package com.example.easyparking;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyparking.Usuarios.Adapter;
import com.example.easyparking.Usuarios.Aparcamiento;
import com.example.easyparking.Usuarios.Vehiculo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatosParkingActivity extends AppCompatActivity {

    TextView textViewNombre, textViewCalle, textViewPrecio, textViewHorario;
    Button iniciarEstacionamiento, btnAceptar, btnCancelar;
    RecyclerView rv;
    List<Vehiculo> vehiculos;
    Vehiculo coche;
    Adapter adapter;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    android.app.AlertDialog.Builder builder;
    LayoutInflater inflater;
    View view;
    android.app.AlertDialog dialog;

    String nombre, calle, horario;
    double precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_parking);

        setToolbar(); //Setear Toolbar como action bar

        textViewNombre = findViewById(R.id.textView_nombre2);
        textViewCalle = findViewById(R.id.textView_calle2);
        textViewPrecio = findViewById(R.id.textView_precio2);
        textViewHorario = findViewById(R.id.textView_horario2);
        iniciarEstacionamiento = findViewById(R.id.iniciar_estacionamiento);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle extras = getIntent().getExtras();
        nombre = extras.getString("Nombre");
        calle = extras.getString("Calle");
        precio = extras.getDouble("Precio");
        horario = extras.getString("Horario");

        textViewNombre.setText(nombre);
        textViewCalle.setText(calle);
        textViewPrecio.setText(String.valueOf(precio) +"€/hora");
        textViewHorario.setText(horario);

        iniciarEstacionamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Indicar el vehiculo que quieres aparcar
                builder = new android.app.AlertDialog.Builder(DatosParkingActivity.this);
                inflater = getLayoutInflater();
                view = inflater.inflate(R.layout.dialog_iniciaraparcamiento, null);
                builder.setView(view);
                dialog = builder.create();
                dialog.show();
                rv = view.findViewById(R.id.recycler);
                rv.setLayoutManager(new LinearLayoutManager(DatosParkingActivity.this));

                btnAceptar = view.findViewById(R.id.btnAceptar);
                btnCancelar = view.findViewById(R.id.btnCancelar);

                vehiculos = new ArrayList<>();

                adapter = new Adapter(vehiculos);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        habilitarBoton();
                        v.requestFocus();
                        //v.setBackgroundColor(Color.GRAY);
                        coche = vehiculos.get(rv.getChildPosition(v));
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Obtener fecha y hora
                        String fecha = obtenerFecha("dd-MM-yyyy HH:mm:ss");

                        //Escribir en aparcamiento en la base de datos
                        Aparcamiento aparcamiento = new Aparcamiento(coche, fecha, "Parking", calle, true, precio);
                        String key = coche.getMatricula().concat("_").concat(fecha); //La clave de la base de datos consta de la matricula del coche, la fecha del aparcamiento y la hora del aparcamiento
                        myRef.child(user.getUid()).child("aparcamientos en curso").child(key).setValue(aparcamiento);
                        dialog.cancel();
                    }
                });
            }
        });
    }

    private String obtenerFecha (String formato) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(formato);
        return sdf.format(date);
    }

    private void habilitarBoton() {
        btnAceptar.setEnabled(true);
        btnAceptar.setTextColor(Color.WHITE);
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
