package com.example.biblioteca_digital.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteca_digital.Adapters.RecipeAdapter;
import com.example.biblioteca_digital.Models.Recipe;
import com.example.biblioteca_digital.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RadioGroup filterRadioGroup;
    private RadioButton filterAll, filterVegetarian, filterFast, filterDesserts;
    private RecyclerView recipesRecyclerView;

    private RecipeAdapter adapter;
    private List<Recipe> todasLasRecetas = new ArrayList<>();
    private List<Recipe> recetasFiltradas = new ArrayList<>();

    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        filterRadioGroup = findViewById(R.id.filterRadioGroup);
        filterAll = findViewById(R.id.filterAll);
        filterVegetarian = findViewById(R.id.filterVegetarian);
        filterFast = findViewById(R.id.filterFast);
        filterDesserts = findViewById(R.id.filterDesserts);
        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);

        recipesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new RecipeAdapter(recetasFiltradas, this::abrirDetallesReceta);
        recipesRecyclerView.setAdapter(adapter);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Cargar datos desde Firebase
        cargarRecetasDesdeFirebase();

        // Búsqueda
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarRecetas(s.toString());
            }
        });

        // Filtros
        filterRadioGroup.setOnCheckedChangeListener((group, checkedId) -> aplicarFiltros());
    }

    // Leer Firebase (usa los mismos campos que MainActivity)
    private void cargarRecetasDesdeFirebase() {
        db.collection("recetas")
                .get()
                .addOnSuccessListener(query -> {

                    todasLasRecetas.clear();

                    for (QueryDocumentSnapshot doc : query) {

                        Recipe receta = new Recipe();

                        // ID del documento
                        receta.setId(doc.getId());
                        // Usar los nombres de campo que tienes en Firestore
                        receta.setNombre(doc.getString("titulo"));
                        receta.setCategoria(doc.getString("tipo"));
                        receta.setTiempo(doc.getString("tiempo"));
                        receta.setDificultad(doc.getString("dificultad"));
                        receta.setDescripcion(doc.getString("descripcion"));

                        // URL de imagen (campo "imagen")
                        receta.setImagenUrl(doc.getString("imagen"));

                        // INGREDIENTES (mismo formato que en MainActivity)
                        List<String> ingredientesList = new ArrayList<>();
                        if (doc.contains("ingredientes")) {
                            Map<String, Object> ingMap = (Map<String, Object>) doc.get("ingredientes");
                            for (Object value : ingMap.values()) {
                                Map<String, Object> ing = (Map<String, Object>) value;
                                String nombre = ing.get("nombre") != null ? ing.get("nombre").toString() : "";
                                String cantidad = ing.get("cantidad") != null ? ing.get("cantidad").toString() : "";
                                ingredientesList.add((cantidad != null && !cantidad.isEmpty() ? cantidad + " " : "") + nombre);
                            }
                        }
                        receta.setIngredientes(ingredientesList);

                        // PASOS (mismo formato que en MainActivity)
                        List<String> pasosList = new ArrayList<>();
                        if (doc.contains("pasos")) {
                            Map<String, Object> pasosMap = (Map<String, Object>) doc.get("pasos");
                            for (Object value : pasosMap.values()) {
                                Map<String, Object> paso = (Map<String, Object>) value;
                                String desc = paso.get("descripcion") != null ? paso.get("descripcion").toString() : "";
                                pasosList.add(desc);
                            }
                        }
                        receta.setPasos(pasosList);

                        todasLasRecetas.add(receta);
                    }

                    recetasFiltradas.clear();
                    recetasFiltradas.addAll(todasLasRecetas);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar recetas: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void filtrarRecetas(String termino) {
        String filterCategoria = obtenerCategoriaSelecionada();
        recetasFiltradas.clear();

        String terminoLower = termino == null ? "" : termino.toLowerCase();

        for (Recipe recipe : todasLasRecetas) {
            String nombre = recipe.getNombre() != null ? recipe.getNombre() : "";
            String categoria = recipe.getCategoria() != null ? recipe.getCategoria() : "";

            boolean coincideNombre = nombre.toLowerCase().contains(terminoLower);
            boolean coincideCategoria = filterCategoria.equals("Todas") || categoria.equals(filterCategoria);

            if (coincideNombre && coincideCategoria) {
                recetasFiltradas.add(recipe);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void aplicarFiltros() {
        filtrarRecetas(searchEditText.getText().toString());
    }

    private String obtenerCategoriaSelecionada() {
        int selectedId = filterRadioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.filterVegetarian) return "Vegetariana";
        else if (selectedId == R.id.filterFast) return "Rápida";
        else if (selectedId == R.id.filterDesserts) return "Postres";

        return "Todas";
    }

    private void abrirDetallesReceta(Recipe recipe) {
        Intent intent = new Intent(SearchActivity.this, com.example.biblioteca_digital.Activities.RecipeDetailActivity.class);

        // Pasar recipeId (necesario para favoritos persistentes)
        intent.putExtra("recipeId", recipe.getId());
        // Mantener las otras extras (coincidentes con RecipeDetailActivity)
        intent.putExtra("recipeName", recipe.getNombre());
        intent.putExtra("recipeCategory", recipe.getCategoria());
        intent.putExtra("recipeTime", recipe.getTiempo());
        intent.putExtra("recipeDifficulty", recipe.getDificultad());
        intent.putExtra("recipeDescription", recipe.getDescripcion());
        // NOTE: RecipeDetailActivity espera "recipeImageUrl"
        intent.putExtra("recipeImageUrl", recipe.getImagenUrl());
        intent.putStringArrayListExtra("recipeIngredients", new ArrayList<>(recipe.getIngredientes()));
        intent.putStringArrayListExtra("recipeSteps", new ArrayList<>(recipe.getPasos()));
        startActivity(intent);
    }
}