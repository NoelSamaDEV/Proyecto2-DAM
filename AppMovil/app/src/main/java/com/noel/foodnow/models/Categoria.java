package com.noel.foodnow.models;

public class Categoria {
    private int id;
    private String nombre;
    private String imagenUrl; // Aquí irá tu futura URL (ej: "https://imgur.com/...")

    public Categoria(int id, String nombre, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getImagenUrl() { return imagenUrl; }
}