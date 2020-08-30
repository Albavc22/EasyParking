package com.example.easyparking.Modelo;

public class Parking {

    String Calle;
    String Horario;
    String Nombre;
    double Precio;

    public Parking() {
    }

    public Parking(String nombre, String calle, double precio, String horario) {
        this.Nombre = nombre;
        this.Calle = calle;
        this.Precio = precio;
        this.Horario = horario;
    }

    public String getCalle() {
        return Calle;
    }

    public void setCalle(String calle) {
        Calle = calle;
    }

    public String getHorario() {
        return Horario;
    }

    public void setHorario(String horario) {
        Horario = horario;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public double getPrecio() {
        return Precio;
    }

    public void setPrecio(double precio) {
        Precio = precio;
    }
}
