package com.example.biblioteca_digital.Models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private int id;
    private String nombre;
    private String categoria;
    private String tiempo;
    private String dificultad;
    private int imagenResId;
    private List<String> ingredientes;
    private List<String> pasos;
    private String descripcion;
    private boolean esFavorita;

    public Recipe(int id, String nombre, String categoria, String tiempo,
                  String dificultad, int imagenResId, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.tiempo = tiempo;
        this.dificultad = dificultad;
        this.imagenResId = imagenResId;
        this.descripcion = descripcion;
        this.ingredientes = new ArrayList<>();
        this.pasos = new ArrayList<>();
        this.esFavorita = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTiempo() { return tiempo; }
    public void setTiempo(String tiempo) { this.tiempo = tiempo; }

    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    public int getImagenResId() { return imagenResId; }
    public void setImagenResId(int imagenResId) { this.imagenResId = imagenResId; }

    public List<String> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<String> ingredientes) { this.ingredientes = ingredientes; }

    public List<String> getPasos() { return pasos; }
    public void setPasos(List<String> pasos) { this.pasos = pasos; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isEsFavorita() { return esFavorita; }
    public void setEsFavorita(boolean esFavorita) { this.esFavorita = esFavorita; }

    public void agregarIngrediente(String ingrediente) {
        this.ingredientes.add(ingrediente);
    }

    public void agregarPaso(String paso) {
        this.pasos.add(paso);
    }
}