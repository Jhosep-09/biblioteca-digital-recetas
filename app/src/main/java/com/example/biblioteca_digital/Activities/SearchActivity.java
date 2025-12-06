package com.example.biblioteca_digital.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteca_digital.Adapters.RecipeAdapter;
import com.example.biblioteca_digital.MainActivity;
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

    // <- nuevo: para recordar qué filtro llegó desde MainActivity
    private String filtroInicial;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        // 🟢 LEER FILTRO QUE LLEGA DESDE MAINACTIVITY
        filtroInicial = getIntent().getStringExtra("filtro");
        if (filtroInicial != null) {
            switch (filtroInicial) {
                case "Vegetariana":
                    filterVegetarian.setChecked(true);
                    break;
                case "Rápida":
                    filterFast.setChecked(true);
                    break;
                case "Postres":
                    filterDesserts.setChecked(true);
                    break;
                case "Todas":
                default:
                    filterAll.setChecked(true);
                    break;
            }
        } else {
            // si no vino nada, por defecto "Todas"
            filterAll.setChecked(true);
        }

        // Cargar datos desde Firebase
        cargarRecetasDesdeFirebase();

        // Búsqueda por texto
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarRecetas(s.toString());
            }
        });

        // Filtros por categoría (radio buttons)
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

                    // 🔴 ANTES: siempre mostrabas todas
                    // recetasFiltradas.clear();
                    // recetasFiltradas.addAll(todasLasRecetas);
                    // adapter.notifyDataSetChanged();

                    // ✅ AHORA: aplica el filtro (texto + categoría)
                    aplicarFiltros();
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
            boolean coincideCategoria =
                    filterCategoria.equals("Todas")
                            || categoria.equalsIgnoreCase(filterCategoria);

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
        else if (selectedId == R.id.filterFast) return "Rapido";
        else if (selectedId == R.id.filterDesserts) return "Postres";
        else if (selectedId == R.id.filterCarnes) return "Carnes";
        else if (selectedId == R.id.filterParrillas) return "Parrillas";

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toobar2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.menu_favoritos) {
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        } else if (id == R.id.menu_perfil) {
            Toast.makeText(this, "Perfil:", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_cerrar) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
