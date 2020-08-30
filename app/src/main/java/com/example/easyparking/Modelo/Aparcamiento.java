package com.example.easyparking.Modelo;

public class Aparcamiento {

    Vehiculo vehiculo;
    String fecha;
    Double latitud;
    Double longitud;
    String zona;
    String calle;
    Boolean aparcado;
    double precio;

    public Aparcamiento() {

    }

    public Aparcamiento(Vehiculo vehiculo, String fecha, String zona, String calle, Boolean aparcado, double precio) {
        this.vehiculo = vehiculo;
        this.fecha = fecha;
        this.zona = zona;
        this.calle = calle;
        this.aparcado = aparcado;
        this.precio = precio;
    }

    public Aparcamiento(Vehiculo vehiculo, String fecha, Double latitud, Double longitud, String zona, String calle, Boolean aparcado, double precio) {
        this.vehiculo = vehiculo;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.zona = zona;
        this.calle = calle;
        this.aparcado = aparcado;
        this.precio = precio;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public Boolean getAparcado() {
        return aparcado;
    }

    public void setAparcado(Boolean aparcado) {
        this.aparcado = aparcado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
