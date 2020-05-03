package com.example.easyparking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AparcamientoRegistradoActivity extends AppCompatActivity {

    TextView matricula, modelo, fecha, hora, calle, zona, zona_pago, precio_hora, duracion_estacionamiento, precio_total;
    Button finalizarEstacionamiento, btnCancelar, btnRealizarPago;

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
        final String stringZona = extras.getString("Zona");

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
                /*String key = stringMatricula.concat("_").concat(stringFecha).concat(" ").concat(stringHora);

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
                });*/
                //Mostrar los datos del pago
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AparcamientoRegistradoActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_datos_pago, null);
                builder.setView(view);
                final android.app.AlertDialog dialog = builder.create();
                dialog.show();
                double precio = 0;
                double precio_final = 0.00;

                btnCancelar = view.findViewById(R.id.btnCancelar);
                btnRealizarPago = view.findViewById(R.id.btnRealizarPago);
                zona_pago = view.findViewById(R.id.zona_pago);
                precio_hora = view.findViewById(R.id.precio_hora);
                duracion_estacionamiento = view.findViewById(R.id.duracion_estacionamiento);
                precio_total = view.findViewById(R.id.precio_total);

                zona_pago.setText("Zona: " +stringZona);

                if (stringZona.equalsIgnoreCase("Zona Verde")) { //Si esta en la zona verde
                    precio = 0.85;
                } else if (stringZona.equalsIgnoreCase("Zona Azul")) { //Si esta en la zona azul
                    precio = 1.20;
                }

                precio_hora.setText("Precio por hora: " +precio +"€");

                //Calcular el tiempo que ha estado el usuario en el aparcamiento
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String fechaIni = stringFecha.concat(" ").concat(stringHora);
                Date fechaInicial = null;
                try {
                    fechaInicial = format.parse(fechaIni);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                final Date fechaFinal = calendar.getTime();
                long diferencia = fechaFinal.getTime() - fechaInicial.getTime();

                long segsMilli = 1000;
                long minsMilli = segsMilli * 60;
                long horasMilli = minsMilli * 60;

                long horasTranscurridas = diferencia / horasMilli;
                diferencia = diferencia % horasMilli;

                long minutosTrancurridos = diferencia / minsMilli;
                diferencia = diferencia % minsMilli;

                long segsTranscurridos = diferencia / segsMilli;

                //Poner información en el marcador
                DecimalFormat df = new DecimalFormat("#.00");
                duracion_estacionamiento.setText("Tiempo estacionado: " +horasTranscurridas +":" +minutosTrancurridos +":" +segsTranscurridos);

                if (!stringZona.equalsIgnoreCase("Zona Gratis")) {
                    precio_final = ((double)horasTranscurridas + ((double)minutosTrancurridos/60) + ((double)segsTranscurridos/3600)) * precio;
                    precio_total.setText("Total a pagar: " +(df.format(precio_final)) +"€");
                } else {
                    precio_total.setText("Total a pagar: " +precio_final +"€");
                }

                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                btnRealizarPago.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (stringZona.equalsIgnoreCase("Zona Gratis")) { //Si el coche esta aparcado en la zona gratis
                            //Poner en aparcamiento como finalizado en la base de datos
                            String key = stringMatricula.concat("_").concat(stringFecha).concat(" ").concat(stringHora);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                            myRef.child(user.getUid()).child("aparcamientos en curso").child(key).child("fecha").setValue(sdf.format(fechaFinal));

                            myRef.child(user.getUid()).child("aparcamientos en curso").child(key).child("aparcado").setValue(false);
                            dialog.cancel();
                            finish();
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
