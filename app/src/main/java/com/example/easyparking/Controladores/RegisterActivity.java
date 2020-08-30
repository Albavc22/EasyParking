package com.example.easyparking.Controladores;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.easyparking.Modelo.Usuario;
import com.example.easyparking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    Button buttonRegister;
    EditText editTextEmail, editTextPass, editTextNombre, editTextPass2;
    TextView textViewErrorPass, textViewErrorEmail;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setToolbar(); //Setear Toolbar como action bar

        buttonRegister = findViewById(R.id.check_button);
        editTextEmail = findViewById(R.id.check_email);
        editTextPass =  findViewById(R.id.check_password);
        editTextNombre = findViewById(R.id.check_nombre);
        editTextPass2 = findViewById(R.id.check_password2);
        textViewErrorPass = findViewById(R.id.errorPass);
        textViewErrorEmail = findViewById(R.id.errorEmail);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = editTextNombre.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPass.getText().toString();
                String password2 = editTextPass2.getText().toString();

                if (validarEmail(email)) {
                    if (password.equals(password2)) { //Si las dos contraseñas son iguales
                        if (password.length() > 6) { //Si la contraseña tiene más de 6 caracteres
                            registrar(email, password, nombre);
                        } else {
                            textViewErrorPass.setText(R.string.error_contrasena_caracteres);
                            editTextPass.requestFocus();
                            textViewErrorEmail.setText("");
                        }

                    } else {
                        textViewErrorPass.setText(R.string.error_contrasenas_iguales);
                        editTextPass2.requestFocus();
                        textViewErrorEmail.setText("");
                    }

                } else {
                    textViewErrorEmail.setText(R.string.email_no_valido);
                    editTextEmail.requestFocus();
                    textViewErrorPass.setText("");
                }
            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
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

        editTextNombre.addTextChangedListener(new TextWatcher() {
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

        editTextPass.addTextChangedListener(new TextWatcher() {
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

        editTextPass2.addTextChangedListener(new TextWatcher() {
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

    //Metodo para validar el email
    private boolean validarEmail (String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void habilitarBoton () {
        if ((!editTextPass.getText().toString().isEmpty()) && (!editTextEmail.getText().toString().isEmpty()) && (!editTextNombre.getText().toString().isEmpty()) && (!editTextPass2.getText().toString().isEmpty())) {
            buttonRegister.setEnabled(true);
            buttonRegister.setTextColor(Color.WHITE);
        } else {
            buttonRegister.setEnabled(false);
            buttonRegister.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    private void registrar(final String email, String pass, final String nombre) {
        final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
        mDialog.setMessage("Espere un momento...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) { //Si se puede enviar el correo de verificación al usuario
                                AlertDialog.Builder emailVerification = new AlertDialog.Builder(RegisterActivity.this);
                                emailVerification.setMessage(R.string.verificar_email_mensaje)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                AlertDialog titulo = emailVerification.create();
                                titulo.setTitle(R.string.verificar_cuenta);
                                titulo.show();
                                //Escribir el usuario en la base de datos
                                Usuario usuario = new Usuario(nombre, email, false);
                                String key = user.getUid();
                                myRef.child(key).setValue(usuario);

                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nombre).build();
                                user.updateProfile(profileChangeRequest);
                            } else { //Si no se puede mandar el correo de verificación al usuario
                                AlertDialog.Builder emailVerification = new AlertDialog.Builder(RegisterActivity.this);
                                emailVerification.setMessage(R.string.verificar_email_mensaje_error)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                AlertDialog titulo = emailVerification.create();
                                titulo.setTitle(R.string.verificar_cuenta);
                                titulo.show();
                            }
                            mDialog.dismiss();
                        }
                    });
                } else {
                    //Controlar que el email no este ya registrado
                    textViewErrorEmail.setText(R.string.error_email_usado);
                    editTextEmail.requestFocus();
                    textViewErrorPass.setText("");
                    mDialog.dismiss();
                }
            }
        });
    }
}
