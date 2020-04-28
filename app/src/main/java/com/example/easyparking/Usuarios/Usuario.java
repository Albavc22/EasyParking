package com.example.easyparking.Usuarios;

import java.util.ArrayList;

public class Usuario {
    String nombre;
    String correoElectronico;
    ArrayList<Vehiculo> vehiculos = new ArrayList<Vehiculo>();

    public Usuario() {
    }

    public Usuario(String nombre, String correoElectronico) {
        this.nombre = nombre;
        this.correoElectronico = correoElectronico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
}
