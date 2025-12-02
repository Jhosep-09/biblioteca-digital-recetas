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

import java.text.Normalizer;
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

        db = FirebaseFirestore.getInstance();

        cargarRecetasDesdeFirebase();

        // BÚSQUEDA
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarRecetas(s.toString());
            }
        });

        // FILTROS
        filterRadioGroup.setOnCheckedChangeListener((group, id) -> aplicarFiltros());
    }

    private void cargarRecetasDesdeFirebase() {
        db.collection("recetas")
                .get()
                .addOnSuccessListener(query -> {

                    todasLasRecetas.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        Recipe receta = new Recipe();

                        receta.setId(doc.getId());
                        receta.setNombre(doc.getString("nombre"));
                        receta.setCategoria(doc.getString("categoria"));
                        receta.setTiempo(doc.getString("tiempo"));
                        receta.setDificultad(doc.getString("dificultad"));
                        receta.setDescripcion(doc.getString("descripcion"));
                        receta.setImagenUrl(doc.getString("imagenUrl"));

                        List<String> ing = (List<String>) doc.get("ingredientes");
                        receta.setIngredientes(ing != null ? ing : new ArrayList<>());

                        List<String> pas = (List<String>) doc.get("pasos");
                        receta.setPasos(pas != null ? pas : new ArrayList<>());

                        todasLasRecetas.add(receta);
                    }

                    recetasFiltradas.clear();
                    recetasFiltradas.addAll(todasLasRecetas);
                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando recetas", Toast.LENGTH_SHORT).show()
                );
    }

    private void filtrarRecetas(String termino) {

        termino = normalizar(termino);

        String categoria = obtenerCategoriaSeleccionada();

        recetasFiltradas.clear();

        for (Recipe r : todasLasRecetas) {

            String nombreNormalizado = normalizar(r.getNombre() != null ? r.getNombre() : "");
            String categoriaReceta = r.getCategoria() != null ? r.getCategoria() : "";

            boolean coincideNombre = nombreNormalizado.contains(termino);
            boolean coincideCategoria = categoria.equals("Todas") ||
                    categoriaReceta.equalsIgnoreCase(categoria);

            if (coincideNombre && coincideCategoria) {
                recetasFiltradas.add(r);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void aplicarFiltros() {
        filtrarRecetas(searchEditText.getText().toString());
    }

    private String obtenerCategoriaSeleccionada() {
        int id = filterRadioGroup.getCheckedRadioButtonId();

        if (id == R.id.filterVegetarian) return "Vegetariana";
        if (id == R.id.filterFast) return "Rápida";
        if (id == R.id.filterDesserts) return "Postres";

        return "Todas";
    }


    private String normalizar(String texto) {
        texto = texto.toLowerCase();
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
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
        intent.putStringArrayListExtra("recipeIngredients",
                new ArrayList<>(recipe.getIngredientes()));
        intent.putStringArrayListExtra("recipeSteps",
                new ArrayList<>(recipe.getPasos()));
        startActivity(intent);
    }
}
