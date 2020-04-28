package com.example.easyparking;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VehiculoRegistradoActivity extends AppCompatActivity {

    TextView textViewMatricula, textViewModelo;
    Button eliminarVehiculo;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo_registrado);

        setToolbar(); //Setear Toolbar como action bar

        textViewMatricula = findViewById(R.id.textView_matricula2);
        textViewModelo = findViewById(R.id.textView_modelo2);
        eliminarVehiculo = findViewById(R.id.eliminar_vehiculo);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle extras = getIntent().getExtras();
        final String matricula = extras.getString("Matricula");
        String modelo = extras.getString("Modelo");

        textViewMatricula.setText(matricula);
        textViewModelo.setText(modelo);

        eliminarVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Eliminar el vehiculo de la base de datos
                myRef.child(user.getUid()).child("vehiculos").child(matricula).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AlertDialog.Builder emailVerification = new AlertDialog.Builder(VehiculoRegistradoActivity.this);
                            emailVerification.setMessage("El vehículo ha sido eliminado con éxito")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            AlertDialog titulo = emailVerification.create();
                            titulo.setTitle("Eliminar vehículo");
                            titulo.show();
                        }
                    }
                });
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
