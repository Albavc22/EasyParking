package com.example.easyparking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    EditText editTextNombre, editTextEmail;
    TextView textViewErrorEmail;
    Button buttonCambiarImagen, buttonEliminarCuenta, buttonCambiarPass;
    private CircleImageView circleImageView;
    private UploadTask uploadTask;

    private final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    private StorageReference storageReference;

    //Usuario
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference myRef;

    //Clave de la base de datos donde se va a escribir el usuario
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        setToolbar(); //Setear Toolbar como action bar

        editTextNombre = (EditText) findViewById(R.id.editText_nombre);
        editTextEmail = (EditText) findViewById(R.id.editText_email);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        buttonCambiarImagen = (Button) findViewById(R.id.cambiar_foto);
        textViewErrorEmail = (TextView) findViewById(R.id.errorEmail);
        buttonEliminarCuenta = (Button) findViewById(R.id.eliminar_cuenta);
        buttonCambiarPass = (Button) findViewById(R.id.cambiar_password);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String nombreUsuario = user.getDisplayName();
            String emailUsuario = user.getEmail();
            Uri imagenPerfil = user.getPhotoUrl();

            editTextEmail.setText(emailUsuario);
            editTextNombre.setText(nombreUsuario);
            circleImageView.setImageURI(imagenPerfil);
        }

        //Cambiar la foto de perfil
        buttonCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        //FirebaseStorage
        storageReference = FirebaseStorage.getInstance().getReference();

        //FirebaseDatabase
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        key = user.getUid();

        //Poner foto de perfil
        if (user.getPhotoUrl() == null) {
            circleImageView.setImageResource(R.drawable.foto_perfil);
        } else {
            Glide.with(PerfilActivity.this).load(user.getPhotoUrl()).fitCenter().centerCrop().into(circleImageView);
        }

       //Eliminar la cuenta del usuario
       buttonEliminarCuenta.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog.Builder eliminarCuenta = new AlertDialog.Builder(PerfilActivity.this);
               eliminarCuenta.setMessage("¿Está seguro de que desea eliminar la cuenta?")
                       .setCancelable(false)
                       .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               user.delete();

                               //Eliminar el usuario de la base de datos
                               myRef.child(key).removeValue();

                               Intent i = new Intent(PerfilActivity.this, MainActivity.class);
                               startActivity(i);
                           }
                       })
                       .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                           }
               });
               AlertDialog titulo = eliminarCuenta.create();
               titulo.setTitle("Eliminar cuenta");
               titulo.show();
           }
       });

        //Cambiar la contraseña del usuario
        buttonCambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PerfilActivity.this, PasswordActivity.class);
                startActivity(i);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_perfil, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check:
                comprobarDatos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void comprobarDatos() {
        textViewErrorEmail.setText("");

        //Comprobar si el nombre ha cambiado y si ha cambiado actualizarlo
        if (!(editTextNombre.getText().toString()).equals(user.getDisplayName())) {
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(editTextNombre.getText().toString()).build();
            user.updateProfile(profileChangeRequest);

            //Modificar el nombre en la base de datos
            myRef.child(key).child("nombre").setValue(editTextNombre.getText().toString());

            if ((editTextEmail.getText().toString()).equalsIgnoreCase(user.getEmail())) { //Si el correo no se ha cambiado
                AlertDialog.Builder emailVerification = new AlertDialog.Builder(PerfilActivity.this);
                emailVerification.setMessage("Los datos han sido cambiados correctamente")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog titulo = emailVerification.create();
                titulo.setTitle("Modificacion de datos");
                titulo.show();
            }
        }

        //Comprobar si el correo ha cambiado y si ha cambiado actualizarlo y enviar un correo de verificacion a la nueva cuenta
        if (!(editTextEmail.getText().toString()).equalsIgnoreCase(user.getEmail())) {
            if (validarEmail(editTextEmail.getText().toString())) {
                user.updateEmail(editTextEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) { //Si se puede enviar el correo de verificación al usuario
                                        AlertDialog.Builder emailVerification = new AlertDialog.Builder(PerfilActivity.this);
                                        emailVerification.setMessage("Se le enviará un email a la dirección especificada para verificar la cuenta")
                                                .setCancelable(false)
                                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Modificar el email en la base de datos
                                                        myRef.child(key).child("correoElectronico").setValue(editTextEmail.getText().toString());

                                                        Intent i = new Intent(PerfilActivity.this, MainActivity.class);
                                                        startActivity(i);
                                                    }
                                                });
                                        AlertDialog titulo = emailVerification.create();
                                        titulo.setTitle("Verificar la cuenta");
                                        titulo.show();
                                    } else { //Si no se puede mandar el correo de verificación al usuario
                                        AlertDialog.Builder emailVerification = new AlertDialog.Builder(PerfilActivity.this);
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
                                }
                            });

                        } else {
                            //Controlar que el email no este ya registrado
                            textViewErrorEmail.setText("La dirección de email ya está siendo usada por otra cuenta");
                            editTextEmail.requestFocus();
                        }
                    }
                });
            } else {
                textViewErrorEmail.setText("Email no válido");
                editTextEmail.requestFocus();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            final ProgressDialog mProgressDialog = new ProgressDialog(PerfilActivity.this);
            mProgressDialog.setMessage("Espere un momento...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            Uri uri = data.getData();
            final StorageReference filePath = storageReference.child("fotos").child(uri.getLastPathSegment());
            uploadTask = filePath.putFile(uri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                        user.updateProfile(profileChangeRequest);

                        Glide.with(PerfilActivity.this).load(downloadUri).fitCenter().centerCrop().into(circleImageView);
                        mProgressDialog.dismiss();
                    }
                }
            });

        }
    }

    //Metodo para validar el email
    private boolean validarEmail (String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
