package com.example.easyparking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button buttonRegister, buttonSignIn, buttonResPass;
    EditText editTextEmail, editTextPass;

    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar(); //Setear Toolbar como action bar

        buttonRegister = findViewById(R.id.register_button);
        buttonSignIn = findViewById(R.id.signin_button);
        buttonResPass = findViewById(R.id.password_button);
        editTextEmail = findViewById(R.id.login_email);
        editTextPass = findViewById(R.id.login_password);
        mAuth = FirebaseAuth.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInicio = editTextEmail.getText().toString();
                String passInicio = editTextPass.getText().toString();
                iniciarSesion(emailInicio, passInicio);
            }
        });

        buttonResPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_recuperarpasss, null);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();

                final EditText email = view.findViewById(R.id.email);
                final Button btnEnviar = view.findViewById(R.id.btnEnviar);
                Button btnCancelar = view.findViewById(R.id.btnCancelar);
                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                final TextView textViewErrorEmail = view.findViewById(R.id.errorEmail);

                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                email.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        btnEnviar.setEnabled(true);
                        btnEnviar.setTextColor(Color.WHITE);
                    }
                });

                btnEnviar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String emailRecPass = email.getText().toString();

                        if (validarEmail(emailRecPass)) { //Si el email que hemos introducido es valido
                            mDialog.setMessage("Espere un momento...");
                            mDialog.setCanceledOnTouchOutside(false);
                            mDialog.show();

                            mAuth.sendPasswordResetEmail(emailRecPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, R.string.send_password_exito, Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    } else {
                                        //Informar de que no se ha podido enviar el correo
                                        Toast.makeText(MainActivity.this, R.string.send_password_fallo, Toast.LENGTH_SHORT).show();
                                    }

                                    mDialog.dismiss();
                                }
                            });

                        } else { //Si el email que hemos introducido no es valido
                            textViewErrorEmail.setText(R.string.email_no_valido);
                        }
                    }
                });
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

        //No se si hace falta
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) { //Comprueba tanto cuando iniciamos sesion como cuando la cerramos
                FirebaseUser user = firebaseAuth.getCurrentUser(); //FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) { //Si el usuario ya ha iniciado sesion
                    Intent i = new Intent(MainActivity.this, MapaActivity.class);
                    startActivity(i);
                } else { //Si ha cerrado sesion
                    //Log.i("SESION", "sesion cerrada");
                }
            }
        };
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }*/
    }

    private void habilitarBoton () {
        if ((!editTextPass.getText().toString().isEmpty()) && (!editTextEmail.getText().toString().isEmpty())) {
            buttonSignIn.setEnabled(true);
            //buttonSignIn.setTextColor(Color.WHITE);
        } else {
            buttonSignIn.setEnabled(false);
            //buttonSignIn.setTextColor(Color.parseColor("#9E9E9E"));
        }
    }

    private void iniciarSesion (final String email, final String pass) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        Intent i = new Intent(MainActivity.this, MapaActivity.class);
                        startActivity(i);
                    } else {
                        AlertDialog.Builder errorEmailVerification = new AlertDialog.Builder(MainActivity.this);
                        errorEmailVerification.setMessage(R.string.error_email_mensaje2)
                                .setCancelable(false)
                                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog titulo = errorEmailVerification.create();
                        titulo.setTitle(R.string.error_email_titulo);
                        titulo.show();
                    }

                } else {
                    AlertDialog.Builder errorSignIn = new AlertDialog.Builder(MainActivity.this);
                    errorSignIn.setMessage(R.string.usuario_contraseña_incorrecto)
                            .setCancelable(false)
                            .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog titulo = errorSignIn.create();
                    titulo.setTitle(R.string.fallo_inicio_sesion);
                    titulo.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    //Metodo para validar el email
    private boolean validarEmail (String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
