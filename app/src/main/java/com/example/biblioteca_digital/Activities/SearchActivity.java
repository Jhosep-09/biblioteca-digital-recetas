package com.example.biblioteca_digital.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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

    // Leer Firebase
    private void cargarRecetasDesdeFirebase() {
        db.collection("recetas")
                .get()
                .addOnSuccessListener(query -> {

                    todasLasRecetas.clear();

                    for (QueryDocumentSnapshot doc : query) {

                        Recipe receta = new Recipe();

                        // 🔥 ID del documento (string)
                        receta.setId(doc.getId());

                        receta.setNombre(doc.getString("nombre"));
                        receta.setCategoria(doc.getString("categoria"));
                        receta.setTiempo(doc.getString("tiempo"));
                        receta.setDificultad(doc.getString("dificultad"));
                        receta.setDescripcion(doc.getString("descripcion"));

                        // 🔥 URL de imagen desde Firestore
                        receta.setImagenUrl(doc.getString("imagenUrl"));

                        // Ingredientes
                        List<String> ingredientes = (List<String>) doc.get("ingredientes");
                        if (ingredientes != null) receta.setIngredientes(ingredientes);

                        // Pasos
                        List<String> pasos = (List<String>) doc.get("pasos");
                        if (pasos != null) receta.setPasos(pasos);

                        todasLasRecetas.add(receta);
                    }

                    recetasFiltradas.clear();
                    recetasFiltradas.addAll(todasLasRecetas);
                    adapter.notifyDataSetChanged();
                });
    }

    private void filtrarRecetas(String termino) {
        String filterCategoria = obtenerCategoriaSelecionada();
        recetasFiltradas.clear();

        for (Recipe recipe : todasLasRecetas) {
            boolean coincideNombre = recipe.getNombre().toLowerCase().contains(termino.toLowerCase());
            boolean coincideCategoria = filterCategoria.equals("Todas") ||
                    recipe.getCategoria().equals(filterCategoria);

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
        Intent intent = new Intent(SearchActivity.this, RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        intent.putExtra("recipeName", recipe.getNombre());
        intent.putExtra("recipeCategory", recipe.getCategoria());
        intent.putExtra("recipeTime", recipe.getTiempo());
        intent.putExtra("recipeDifficulty", recipe.getDificultad());
        intent.putExtra("recipeImage", recipe.getImagenUrl());
        intent.putExtra("recipeDescription", recipe.getDescripcion());
        intent.putStringArrayListExtra("recipeIngredients", new ArrayList<>(recipe.getIngredientes()));
        intent.putStringArrayListExtra("recipeSteps", new ArrayList<>(recipe.getPasos()));
        startActivity(intent);
    }
}