package com.example.biblioteca_digital.Models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private String id;
    private String nombre;
    private String tipo;
    private String tiempo;
    private String dificultad;
    private String imagenUrl;
    private List<String> ingredientes;
    private List<String> pasos;
    private String descripcion;
    private boolean esFavorita;

    public Recipe() {
        ingredientes = new ArrayList<>();
        pasos = new ArrayList<>();
    }

    public Recipe(String id, String nombre, String tipo, String tiempo,
                  String dificultad, String imagenUrl, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.tiempo = tiempo;
        this.dificultad = dificultad;
        this.imagenUrl = imagenUrl;
        this.descripcion = descripcion;
        this.ingredientes = new ArrayList<>();
        this.pasos = new ArrayList<>();
        this.esFavorita = false;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return tipo; }
    public void setCategoria(String tipo) { this.tipo = tipo; }

    public String getTiempo() { return tiempo; }
    public void setTiempo(String tiempo) { this.tiempo = tiempo; }

    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

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