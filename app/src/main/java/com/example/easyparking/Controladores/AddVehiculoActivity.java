package com.example.easyparking.Controladores;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.easyparking.Modelo.Vehiculo;
import com.example.easyparking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddVehiculoActivity extends AppCompatActivity {

    EditText editTextModelo, editTextMatricula;
    Button addVehiculo;

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehiculo);

        setToolbar(); //Setear Toolbar como action bar

        editTextModelo = findViewById(R.id.modelo);
        editTextMatricula = findViewById(R.id.matricula);
        addVehiculo = findViewById(R.id.add_vehiculo);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");

        user = FirebaseAuth.getInstance().getCurrentUser();

        editTextMatricula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                habilitarBoton();
            }
        });

        editTextModelo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                habilitarBoton();
            }
        });

        addVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Escribir el vehiculo en la base de datos
                Vehiculo vehiculo = new Vehiculo(editTextMatricula.getText().toString(), editTextModelo.getText().toString());
                String key = editTextMatricula.getText().toString();
                myRef.child(user.getUid()).child("vehiculos").child(key).setValue(vehiculo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            AlertDialog.Builder emailVerification = new AlertDialog.Builder(AddVehiculoActivity.this);
                            emailVerification.setMessage(R.string.mensaje_exito)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                            AlertDialog titulo = emailVerification.create();
                            titulo.setTitle(R.string.title_activity_add_vehiculo);
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

    private void habilitarBoton () {
        if ((!editTextMatricula.getText().toString().isEmpty()) && (!editTextModelo.getText().toString().isEmpty())) {
            addVehiculo.setEnabled(true);
            //addVehiculo.setTextColor(Color.WHITE);
        } else {
            addVehiculo.setEnabled(false);
            //addVehiculo.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }
}
