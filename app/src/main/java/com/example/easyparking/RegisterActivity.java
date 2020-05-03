package com.example.easyparking;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easyparking.Usuarios.Usuario;
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

        buttonRegister = (Button) findViewById(R.id.check_button);
        editTextEmail = (EditText) findViewById(R.id.check_email);
        editTextPass = (EditText) findViewById(R.id.check_password);
        editTextNombre = (EditText) findViewById(R.id.check_nombre);
        editTextPass2 = (EditText) findViewById(R.id.check_password2);
        textViewErrorPass = (TextView) findViewById(R.id.errorPass);
        textViewErrorEmail = (TextView) findViewById(R.id.errorEmail);

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
                            textViewErrorPass.setText("La contraseña tiene que tener al menos 6 caracteres");
                            editTextPass2.requestFocus();
                            textViewErrorEmail.setText("");
                        }

                    } else {
                        textViewErrorPass.setText("Las dos contraseñas deben de ser iguales");
                        editTextPass2.requestFocus();
                        textViewErrorEmail.setText("");
                    }

                } else {
                    textViewErrorEmail.setText("Email no válido");
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
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) { //Si se puede enviar el correo de verificación al usuario
                                AlertDialog.Builder emailVerification = new AlertDialog.Builder(RegisterActivity.this);
                                emailVerification.setMessage("Se le enviará un email a la dirección especificada para verificar la cuenta")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                AlertDialog titulo = emailVerification.create();
                                titulo.setTitle("Verificar la cuenta");
                                titulo.show();
                                //Escribir el usuario en la base de datos
                                Usuario usuario = new Usuario(nombre, email);
                                String key = user.getUid();
                                myRef.child(key).setValue(usuario);

                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nombre).build();
                                user.updateProfile(profileChangeRequest);
                            } else { //Si no se puede mandar el correo de verificación al usuario
                                AlertDialog.Builder emailVerification = new AlertDialog.Builder(RegisterActivity.this);
                                emailVerification.setMessage("No se ha podido enviar el correo de verificación al email especificado")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                AlertDialog titulo = emailVerification.create();
                                titulo.setTitle("Verificar la cuenta");
                                titulo.show();
                            }
                            mDialog.dismiss();
                        }
                    });
                } else {
                    //Controlar que el email no este ya registrado
                    textViewErrorEmail.setText("La dirección de email ya está siendo usada por otra cuenta");
                    editTextEmail.requestFocus();
                    textViewErrorPass.setText("");
                    mDialog.dismiss();
                }
            }
        });
    }
}
