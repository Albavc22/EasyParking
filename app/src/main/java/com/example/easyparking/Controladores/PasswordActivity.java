package com.example.easyparking.Controladores;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.easyparking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordActivity extends AppCompatActivity {

    TextView errorPassActual, errorPassNueva, errorPasNueva2;
    EditText passActual, passNueva, passNueva2;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        setToolbar(); //Setear Toolbar como action bar

        errorPassActual = findViewById(R.id.errorPass_actual);
        errorPassNueva = findViewById(R.id.errorPass_nueva);
        errorPasNueva2 = findViewById(R.id.errorPass_nueva2);
        passActual = findViewById(R.id.pass_actual);
        passNueva = findViewById(R.id.pass_nueva);
        passNueva2 = findViewById(R.id.pass_nueva2);

        user = FirebaseAuth.getInstance().getCurrentUser();
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
        inflater.inflate(R.menu.menu_perfil, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check:
                comprobarDatos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void comprobarDatos() {
        //Comprobar que ninguno de los tres editText esta vacio
        if ((passActual.getText().toString().isEmpty()) || (passNueva.getText().toString().isEmpty()) || (passNueva2.getText().toString().isEmpty())) {
            if (passActual.getText().toString().isEmpty()) {
                errorPassActual.setText(R.string.campo_obligatorio);
                passActual.requestFocus();
            } else {
                errorPassActual.setText("");
            }

            if (passNueva.getText().toString().isEmpty()) {
                errorPassNueva.setText(R.string.campo_obligatorio);
                passNueva.requestFocus();
            } else {
                errorPassNueva.setText("");
            }

            if (passNueva2.getText().toString().isEmpty()) {
                errorPasNueva2.setText(R.string.campo_obligatorio);
                passNueva2.requestFocus();
            } else {
                errorPasNueva2.setText("");
            }
        } else if (!(passNueva.getText().toString()).equalsIgnoreCase(passNueva2.getText().toString())){ //Si las contraseñas no son iguales
            errorPasNueva2.setText(R.string.error_contrasenas_iguales);
            errorPassActual.setText("");
            errorPassNueva.setText("");
        } else if (passNueva.getText().toString().length() < 6) {
            errorPassNueva.setText(R.string.error_contrasena_caracteres); //Si la contraseña tiene menos de 6 caracteres
            errorPassActual.setText("");
            errorPasNueva2.setText("");
        } else if ((passActual.getText().toString()).equalsIgnoreCase(passNueva.getText().toString())) { //Si la contraseña actual y la nueva son la misma
            errorPassNueva.setText(R.string.error_misma_contrasena);
            errorPassActual.setText("");
            errorPasNueva2.setText("");
        } else {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), passActual.getText().toString());
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) { //Si el usuario esta autenticado
                        user.updatePassword(passNueva.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    AlertDialog.Builder emailVerification = new AlertDialog.Builder(PasswordActivity.this);
                                    emailVerification.setMessage(R.string.contrasena_correcta)
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });
                                    AlertDialog titulo = emailVerification.create();
                                    titulo.setTitle(R.string.cambiar_contrasena);
                                    titulo.show();
                                }
                            }
                        });
                    } else { //Si el usuario no esta autenticado
                        errorPassNueva.setText("");
                        errorPassActual.setText(R.string.contrasena_incorrecta);
                        errorPasNueva2.setText("");
                    }
                }
            });
        }
    }
}
