package com.example.easyparking;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyparking.Usuarios.Adapter;
import com.example.easyparking.Usuarios.Aparcamiento;
import com.example.easyparking.Usuarios.Vehiculo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int RC_CAMERA_AND_LOCATION = 78;
    private GoogleMap gMap;
    private MapView mapView;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final double LATITUD_CIRCULO1 = 40.986486;
    private static final double LONGITUD_CIRCULO1 = -5.684599;
    private static final double LATITUD_CIRCULO2 = 40.975588;
    private static final double LONGITUD_CIRCULO2 = -5.651783;

    /* Instancia del drawer */
    private DrawerLayout drawerLayout;
    private TextView textNombre, textEmail;
    private CircleImageView circleImageView;
    private FirebaseUser user;
    private NavigationView navigationView;
    private View hView;
    private Switch mapa;

    //Vehiculos
    RecyclerView rv;
    List<Vehiculo> vehiculos;
    Vehiculo coche;
    Adapter adapter;
    FirebaseDatabase database;
    DatabaseReference myRef;

    Button btnAceptar, btnCancelar;

    //Circulos de las zonas del mapa
    CircleOptions circulo1, circulo2;
    Circle circle1, circle2;

    //Array para guardar los marcadores
    HashMap<String, Marker> marcadores;

    SharedPreferences preferences;

    public static long duracionMarcador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        //Coger las preferencias
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        marcadores = new HashMap<>();
        
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        
        setToolbar(); //Setear Toolbar como action bar

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("usuarios");
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Inicializamos el NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        hView = navigationView.getHeaderView(0);
        textNombre = hView.findViewById(R.id.username);
        textEmail = hView.findViewById(R.id.email);
        circleImageView = hView.findViewById(R.id.profile_image);
        mapa = navigationView.getMenu().findItem(R.id.nav_switch).getActionView().findViewById(R.id.switch_mapa);

        mapa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String nombreUsuario = user.getDisplayName();
            String emailUsuario = user.getEmail();

            textEmail.setText(emailUsuario);
            textNombre.setText(nombreUsuario);

            //Poner foto de perfil
            if (user.getPhotoUrl() == null) {
                circleImageView.setImageResource(R.drawable.foto_perfil);
            } else {
                Glide.with(MapaActivity.this).load(user.getPhotoUrl()).fitCenter().centerCrop().into(circleImageView);
            }
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_iniciarEstacionamiento:
                        //Mostrar un mensaje indicando como se tiene que iniciar el estacionamiento
                        android.app.AlertDialog.Builder inciarEstacionamiento = new android.app.AlertDialog.Builder(MapaActivity.this);
                        inciarEstacionamiento.setMessage(R.string.iniciar_estacionamiento_mensaje)
                                .setCancelable(false)
                                .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        android.app.AlertDialog titulo = inciarEstacionamiento.create();
                        titulo.setTitle(R.string.iniciar_estacionamiento_item);
                        titulo.show();
                        añadirMarcador();
                        break;
                    case R.id.nav_estacionamientosCurso:
                        startActivity(new Intent(MapaActivity.this, AparcamientosActivity.class));
                        break;
                    case R.id.nav_vehiculos:
                        startActivity(new Intent(MapaActivity.this, VehiculoActivity.class));
                        break;
                    case R.id.nav_pagarParkings:
                        startActivity(new Intent(MapaActivity.this, ParkingActivity.class));
                        break;
                    case R.id.nav_configuracion:
                        startActivity(new Intent(MapaActivity.this, AjustesActivity.class));
                        break;
                    case R.id.nav_cuenta:
                        startActivity(new Intent(MapaActivity.this, PerfilActivity.class));
                        break;
                    case R.id.nav_ayuda:
                        startActivity(new Intent(MapaActivity.this, ContenedorInstruccionesActivity.class));
                        break;
                    case R.id.nav_log_out:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        break;
                }
                drawerLayout.closeDrawers();

                return true;
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_nav_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

        navigationView = findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);

        circleImageView = hView.findViewById(R.id.profile_image);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String nombreUsuario = user.getDisplayName();
            String emailUsuario = user.getEmail();

            textEmail.setText(emailUsuario);
            textNombre.setText(nombreUsuario);
            if (user.getPhotoUrl() != null) {
                Glide.with(MapaActivity.this).load(user.getPhotoUrl()).fitCenter().centerCrop().into(circleImageView);
            }
        }

        //Cargar marcadores
        comprobarMarcadores();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_CAMERA_AND_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permitir la ubicacion
                    gMap.setMyLocationEnabled(true);
                } else {
                    //Mostrar un alert dialog indicando que se necesita tener los permisos
                    AlertDialog.Builder permisosUbicacion = new AlertDialog.Builder(MapaActivity.this);
                    permisosUbicacion.setMessage(R.string.permisos_mensaje)
                            .setCancelable(false)
                            .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog titulo = permisosUbicacion.create();
                    titulo.setTitle(R.string.permisos_titulo);
                    titulo.show();
                    return;
                }
                return;
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        //Comprobar los permisos de ubicacion
        if (ActivityCompat.checkSelfPermission(MapaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapaActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Mostrar un mensaje de error indicando que no se tienen los permisos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_CAMERA_AND_LOCATION);
        } else {
            gMap.setMyLocationEnabled(true);
        }

        //Asigno un nivel de zoom
        CameraUpdate ZoomCam = CameraUpdateFactory.zoomTo(19);
        gMap.animateCamera(ZoomCam);

        //Establezco un listener para "escuchar" cuando hay cambios de posición
        gMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //Extraigo la Latitud y la Longitud del Listener
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                //Muevo la cámara a mi posición
                CameraUpdate cam = CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
                gMap.moveCamera(cam);
            }
        });

        //Para los controles de la interfaz de usuario
        gMap.setMinZoomPreference(13);
        gMap.setIndoorEnabled(true);
        UiSettings uiSettings = gMap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);

        //Dividir el mapa de salamanca en zonas
        //Zona azul
        circulo1 = new CircleOptions().center(new LatLng(LATITUD_CIRCULO1, LONGITUD_CIRCULO1)).radius(1500);
        circle1 = gMap.addCircle(circulo1);
        circle1.setFillColor(Color.TRANSPARENT);
        circle1.setStrokeColor(Color.BLUE);
        circle1.setStrokeWidth(2f);

        //Zona verde
        circulo2 = new CircleOptions().center(new LatLng(LATITUD_CIRCULO2, LONGITUD_CIRCULO2)).radius(1500);
        circle2 = gMap.addCircle(circulo2);
        circle2.setFillColor(Color.TRANSPARENT);
        circle2.setStrokeColor(Color.GREEN);
        circle2.setStrokeWidth(2f);

        //Mostrar informacion de los marcadores
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Mostrar la informacion del marcador
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MapaActivity.this);
                final LayoutInflater inflater = getLayoutInflater();
                final Aparcamiento marcador = (Aparcamiento) marker.getTag();

                if (marcador.getAparcado()) {
                    View view = inflater.inflate(R.layout.dialog_aparcamiento, null);
                    builder.setView(view);
                    final android.app.AlertDialog dialog = builder.create();
                    dialog.show();

                    Button aceptar = view.findViewById(R.id.aceptar);
                    TextView matricula = view.findViewById(R.id.matricula_aparcamiento);
                    TextView modelo = view.findViewById(R.id.modelo_aparcamiento);
                    TextView fecha = view.findViewById(R.id.fecha_aparcamiento);
                    TextView hora = view.findViewById(R.id.hora_aparcamiento);
                    TextView calle = view.findViewById(R.id.calle_aparcamiento);
                    TextView zona = view.findViewById(R.id.zona_aparcamiento);

                    aceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    //Dividir la fecha
                    String[] tiempo = marcador.getFecha().split(" ");

                    //Poner información en el marcador
                    matricula.append(": " +marcador.getVehiculo().getMatricula());
                    modelo.append(": " +marcador.getVehiculo().getModelo());
                    fecha.append(": " +tiempo[0]);
                    hora.append(": " +tiempo[1]);
                    calle.append(": " +marcador.getCalle());
                    zona.append(": " +marcador.getZona());
                } else {
                    View view = inflater.inflate(R.layout.dialog_aparcamiento_finalizado, null);
                    builder.setView(view);
                    final android.app.AlertDialog dialog = builder.create();
                    dialog.show();

                    Button aceptar = view.findViewById(R.id.aceptar);
                    TextView tiempo = view.findViewById(R.id.tiempo_libre);
                    TextView calle = view.findViewById(R.id.calle_libre);
                    TextView zona = view.findViewById(R.id.zona_libre);

                    aceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    //Calcular el tiempo que lleva libre el aparcamiento
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date fechaInicial = null;
                    try {
                        fechaInicial = format.parse(marcador.getFecha());
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                    Calendar calendar = Calendar.getInstance();
                    Date fechaFinal = calendar.getTime();
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
                    tiempo.append(": " +horasTranscurridas +":" +minutosTrancurridos +":" +segsTranscurridos);
                    calle.append(": " +marcador.getCalle());
                    zona.append(": " +marcador.getZona());
                }
                return true;
            }
        });
    }

    private void añadirMarcador() {
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                //Indicar el vehiculo que quieres aparcar
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MapaActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_iniciaraparcamiento, null);
                builder.setView(view);
                final android.app.AlertDialog dialog = builder.create();
                dialog.show();
                rv = view.findViewById(R.id.recycler);
                rv.setLayoutManager(new LinearLayoutManager(MapaActivity.this));

                btnAceptar = view.findViewById(R.id.btnAceptar);
                btnCancelar = view.findViewById(R.id.btnCancelar);

                vehiculos = new ArrayList<>();

                adapter = new Adapter(vehiculos);
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        habilitarBoton();
                        v.requestFocus();
                        //v.setBackgroundColor(Color.GRAY);
                        coche = vehiculos.get(rv.getChildPosition(v));
                    }
                });
                rv.setAdapter(adapter);

                myRef.child(user.getUid()).child("vehiculos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        vehiculos.removeAll(vehiculos);
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Vehiculo vehiculo = snapshot.getValue(Vehiculo.class);
                            vehiculos.add(vehiculo);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Obtener fecha y hora
                        String fecha = obtenerFecha("dd-MM-yyyy HH:mm:ss");

                        //Obtener la calle en la que se encuentra en marcador
                        List<Address> addresses;
                        Geocoder geo = new Geocoder(MapaActivity.this.getApplicationContext(), Locale.getDefault());
                        addresses = null;
                        String street = null;
                        String [] elementosDireccion;
                        try {
                            addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses.size() > 0) {
                            elementosDireccion = addresses.get(0).getAddressLine(0).split(",");
                            street = elementosDireccion[0];
                        }

                        //Comprobamos en que zona se ha añadido el marcador
                        String zona = null;
                        float[] disResultado = new float[2];
                        double precio = 0.00;

                        Location.distanceBetween(latLng.latitude, latLng.longitude, circle1.getCenter().latitude, circle1.getCenter().longitude, disResultado);
                        if (disResultado[0] > circle1.getRadius()) {
                            //No esta en el primer circulo, comprobamos si esta en el segundo
                            Location.distanceBetween(latLng.latitude, latLng.longitude, circle2.getCenter().latitude, circle2.getCenter().longitude, disResultado);
                            if (disResultado[0] > circle2.getRadius()) {
                                //No esta en el segundo circulo
                                zona = "Zona Gratis";
                            } else {
                                //Esta en el segundo circulo
                                zona = "Zona Verde";
                                precio = 0.85;
                            }
                        } else {
                            //Esta en el primer circulo
                            zona = "Zona Azul";
                            precio = 1.20;
                        }

                        //Escribir en aparcamiento en la base de datos
                        Aparcamiento aparcamiento = new Aparcamiento(coche, fecha, latLng.latitude, latLng.longitude, zona, street, true, precio);
                        String key = coche.getMatricula().concat("_").concat(fecha); //La clave de la base de datos consta de la matricula del coche, la fecha del aparcamiento y la hora del aparcamiento
                        myRef.child(user.getUid()).child("aparcamientos en curso").child(key).setValue(aparcamiento);

                        //Añadir marcador
                        Marker aparacamiento = gMap.addMarker(new MarkerOptions().anchor(0.0f, 1.0f).position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        //Asignar un objeto al marcador
                        aparacamiento.setTag(aparcamiento);
                        marcadores.put(key, aparacamiento);

                        dialog.cancel();
                    }
                });
            }
        });
    }

    private void habilitarBoton() {
        btnAceptar.setEnabled(true);
        //btnAceptar.setTextColor(Color.WHITE);
    }

    private String obtenerFecha (String formato) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(formato);
        return sdf.format(date);
    }

    private void comprobarMarcadores() {
        myRef.child(user.getUid()).child("aparcamientos en curso").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Aparcamiento aparcamiento, tag;
                        Iterator iterator;
                        Map.Entry marcador;
                        Marker aparc;
                        Boolean aparece = false;
                        aparcamiento = snapshot.getValue(Aparcamiento.class);

                        iterator = marcadores.entrySet().iterator();
                        while (iterator.hasNext()) {
                            marcador = (Map.Entry) iterator.next();
                            if (marcador.getKey().equals(snapshot.getKey())) { //Si son el mismo marcador
                                aparece = true;
                                aparc = (Marker) marcador.getValue();
                                tag = (Aparcamiento) aparc.getTag();
                                if (!tag.getAparcado().equals(aparcamiento.getAparcado())) { //Si el coche ya no esta aparcado
                                    aparc.setTag(aparcamiento); //Actualizo el tag del marcador
                                    aparc.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                }
                            }
                        }

                        //Comprobamos si el marcador tiene que seguir apareciendo
                        //Primero obtenemos las preferencias
                        obtenerPreferencias(preferences);
                        if (!aparcamiento.getAparcado()) { //Si el coche ya no esta aparcado
                            //Calcular el tiempo que ha pasado desde que se ha finalizado el aparcamiento
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String fechaIni = aparcamiento.getFecha();
                            Date fechaInicial = null;
                            try {
                                fechaInicial = format.parse(fechaIni);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }

                            Calendar calendar = Calendar.getInstance();
                            Date fechaFinal = calendar.getTime();
                            long diferencia = fechaFinal.getTime() - fechaInicial.getTime();

                            if (diferencia > duracionMarcador) { //Hay que quitar el marcador
                                aparece = true;

                                if (!marcadores.isEmpty()) {
                                    Marker marker = marcadores.get(snapshot.getKey());

                                    if (marker != null) {
                                        marker.remove();
                                    } else {
                                        break;
                                    }
                                }
                                myRef.child(user.getUid()).child("aparcamientos en curso").child(snapshot.getKey()).removeValue();
                                marcadores.remove(snapshot.getKey());
                            }
                        }

                        if (aparece == false && !aparcamiento.getZona().equalsIgnoreCase("Parking")) {
                            LatLng latLng = new LatLng(aparcamiento.getLatitud(), aparcamiento.getLongitud());
                            Marker marker;
                            if (aparcamiento.getAparcado()) {
                                marker = gMap.addMarker(new MarkerOptions().anchor(0.0f, 1.0f).position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            } else {
                                marker = gMap.addMarker(new MarkerOptions().anchor(0.0f, 1.0f).position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            }
                            marker.setTag(aparcamiento);
                            String key = aparcamiento.getVehiculo().getMatricula().concat("_").concat(aparcamiento.getFecha());
                            marcadores.put(key, marker);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void obtenerPreferencias (SharedPreferences preferences) {
        String tiempo = preferences.getString("tiempoMarcadores","15 minutos");

        if (tiempo.equalsIgnoreCase("5 minutos")) {
            duracionMarcador = 300000;
        } else if (tiempo.equalsIgnoreCase("10 minutos")) {
            duracionMarcador = 600000;
        } else if (tiempo.equalsIgnoreCase("15 minutos")) {
            duracionMarcador = 900000;
        } else if (tiempo.equalsIgnoreCase("20 minutos")) {
            duracionMarcador = 1200000;
        } else if (tiempo.equalsIgnoreCase("30 minutos")) {
            duracionMarcador = 1800000;
        } else if (tiempo.equalsIgnoreCase("1 hora")) {
            duracionMarcador = 3600000;
        }
    }
}
