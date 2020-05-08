package com.example.easyparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AparcamientoRegistradoActivity extends AppCompatActivity {

    //PayPal
    private static final String CONFIG_CLIENT_ID = "ARF3aet7oo2C5EgjjO976EwB5Wom-XJvLNb4OkGVXkTfTJ9NFdMUp4kOhGUkX8XrjYOGPbm29vwq3cau";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);


    TextView matricula, modelo, fecha, hora, calle, zona, zona_pago, precio_hora, duracion_estacionamiento, precio_total;
    Button finalizarEstacionamiento, btnCancelar, btnRealizarPago;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    double precio_final;
    String stringMatricula, stringModelo, stringFecha, stringHora, stringCalle, stringZona;
    double precio;
    Date fechaFinal;
    DecimalFormat df;

    android.app.AlertDialog.Builder builder;
    LayoutInflater inflater;
    View view;
    android.app.AlertDialog dialog;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aparcamiento_registrado);

        df = new DecimalFormat("#0.00");

        //Start Paypal Service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

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
        stringMatricula = extras.getString("Matricula");
        stringModelo = extras.getString("Modelo");
        stringFecha = extras.getString("Fecha");
        stringHora = extras.getString("Hora");
        stringCalle = extras.getString("Calle");
        stringZona = extras.getString("Zona");
        precio = extras.getDouble("Precio");

        matricula.setText(stringMatricula);
        modelo.setText(stringModelo);
        fecha.setText(stringFecha);
        hora.setText(stringHora);
        calle.setText(stringCalle);
        zona.setText(stringZona);

        finalizarEstacionamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mostrar los datos del pago
                builder = new android.app.AlertDialog.Builder(AparcamientoRegistradoActivity.this);
                inflater = getLayoutInflater();
                view = inflater.inflate(R.layout.dialog_datos_pago, null);
                builder.setView(view);
                dialog = builder.create();
                dialog.show();
                precio_final = 0.00;

                btnCancelar = view.findViewById(R.id.btnCancelar);
                btnRealizarPago = view.findViewById(R.id.btnRealizarPago);
                zona_pago = view.findViewById(R.id.zona_pago);
                precio_hora = view.findViewById(R.id.precio_hora);
                duracion_estacionamiento = view.findViewById(R.id.duracion_estacionamiento);
                precio_total = view.findViewById(R.id.precio_total);

                zona_pago.setText("Zona: " +stringZona);

                precio_hora.setText("Precio por hora: " +(df.format(precio)) +"€");

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
                fechaFinal = calendar.getTime();
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
                duracion_estacionamiento.setText("Tiempo estacionado: " +horasTranscurridas +":" +minutosTrancurridos +":" +segsTranscurridos);

                if (!stringZona.equalsIgnoreCase("Zona Gratis")) {
                    precio_final = ((double)horasTranscurridas + ((double)minutosTrancurridos/60) + ((double)segsTranscurridos/3600)) * precio;
                    precio_total.setText("Total a pagar: " +String.valueOf(df.format(precio_final)) +"€");
                } else {
                    precio_total.setText("Total a pagar: " +String.valueOf(precio_final) +"€");
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
                        } else {
                            processPayment(precio_final);
                        }
                    }
                });
            }
        });
    }

    private void processPayment(Double precio) {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(precio)), "EUR", "Pago de estacionamiento", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(AparcamientoRegistradoActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", String.valueOf(df.format(precio_final)))
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Poner en aparcamiento como finalizado en la base de datos
                    String key = stringMatricula.concat("_").concat(stringFecha).concat(" ").concat(stringHora);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    myRef.child(user.getUid()).child("aparcamientos en curso").child(key).child("fecha").setValue(sdf.format(fechaFinal));

                    myRef.child(user.getUid()).child("aparcamientos en curso").child(key).child("aparcado").setValue(false);
                    dialog.cancel();
                    finish();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        }

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
