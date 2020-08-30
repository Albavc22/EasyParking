package com.example.easyparking.Modelo;

import java.util.ArrayList;

public class Usuario {
    String nombre;
    String correoElectronico;
    Boolean verificado;
    ArrayList<Vehiculo> vehiculos = new ArrayList<Vehiculo>();
    ArrayList<Aparcamiento> aparcamientos = new ArrayList<Aparcamiento>();

    public ArrayList<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public void setVehiculos(ArrayList<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public ArrayList<Aparcamiento> getAparcamientos() {
        return aparcamientos;
    }

    public void setAparcamientos(ArrayList<Aparcamiento> aparcamientos) {
        this.aparcamientos = aparcamientos;
    }

    public Usuario(String nombre, String correoElectronico, Boolean verificado) {
        this.nombre = nombre;
        this.correoElectronico = correoElectronico;
        this.verificado = verificado;
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

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }
}
