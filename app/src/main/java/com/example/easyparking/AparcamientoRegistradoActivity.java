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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AparcamientoRegistradoActivity extends AppCompatActivity {

    TextView matricula, modelo, fecha, hora, calle, zona;
    Button finalizarEstacionamiento;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aparcamiento_registrado);

        setToolbar(); //Setear Toolbar como action bar

        matricula = findViewById(R.id.textView_matricula2_estacionamiento);
        modelo = findViewById(R.id.textView_modelo2_estacionamiento);
        fecha = findViewById(R.id.textView_fecha2_estacionamiento);
        hora = findViewById(R.id.textView_hora2_estacionamiento);
        calle = findViewById(R.id.textView_calle2_estacionamiento);
        zona = findViewById(R.id.textView_zona2_estacionamiento);
        finalizarEstacionamiento = findViewById(R.id.finalizar_estacionamiento);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Bundle extras = getIntent().getExtras();
        final String stringMatricula = extras.getString("Matricula");
        String stringModelo = extras.getString("Modelo");
        final String stringFecha = extras.getString("Fecha");
        final String stringHora = extras.getString("Hora");
        String stringCalle = extras.getString("Calle");
        String stringZona = extras.getString("Zona");

        matricula.setText(stringMatricula);
        modelo.setText(stringModelo);
        fecha.setText(stringFecha);
        hora.setText(stringHora);
        calle.setText(stringCalle);
        zona.setText(stringZona);

        finalizarEstacionamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Poner en aparcamiento como finalizado en la base de datos
                String key = stringMatricula.concat("_").concat(stringFecha).concat(" ").concat(stringHora);

                //Poner la hora a la que ha finalizado el aparcamiento
                String fecha = obtenerFecha("dd-MM-yyyy HH:mm:ss");
                //String hora = obtenerFecha("HH:mm:ss");
                myRef.child(user.getUid()).child("aparcamientos en curso").child(key).child("fecha").setValue(fecha);

                myRef.child(user.getUid()).child("aparcamientos en curso").child(key).child("aparcado").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AlertDialog.Builder estacionamientoFinalizado = new AlertDialog.Builder(AparcamientoRegistradoActivity.this);
                            estacionamientoFinalizado.setMessage("El estacionamiento ha sido finalizado con éxito")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            AlertDialog titulo = estacionamientoFinalizado.create();
                            titulo.setTitle("Finalizar estacionamiento");
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

    private String obtenerFecha (String formato) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(formato);
        return sdf.format(date);
    }
}
