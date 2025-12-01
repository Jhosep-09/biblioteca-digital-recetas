package com.example.biblioteca_digital;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteca_digital.Activities.LoginActivity;
import com.example.biblioteca_digital.Activities.RecipeDetailActivity;
import com.example.biblioteca_digital.Activities.SearchActivity;
import com.example.biblioteca_digital.Activities.FavoritesActivity;
import com.example.biblioteca_digital.Adapters.RecipeAdapter;
import com.example.biblioteca_digital.Models.Recipe;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnCategoryVegetariana, btnCategoryRapida, btnCategoryPostres, btnCategoryTodas;
    private RecyclerView recetasRecyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnCategoryVegetariana = findViewById(R.id.btnCategoryVegetariana);
        btnCategoryRapida = findViewById(R.id.btnCategoryRapida);
        btnCategoryPostres = findViewById(R.id.btnCategoryPostres);
        btnCategoryTodas = findViewById(R.id.btnCategoryTodas);

        btnCategoryVegetariana.setOnClickListener(v -> irABusquedaConFiltro("Vegetariana"));
        btnCategoryRapida.setOnClickListener(v -> irABusquedaConFiltro("Rápida"));
        btnCategoryPostres.setOnClickListener(v -> irABusquedaConFiltro("Postres"));
        btnCategoryTodas.setOnClickListener(v -> irABusquedaConFiltro("Todas"));

        // RecyclerView
        recetasRecyclerView = findViewById(R.id.recetasRecyclerView1);
        recetasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recetas = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recetas, recipe -> {
            Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
            intent.putExtra("recipeName", recipe.getNombre());
            intent.putExtra("recipeCategory", recipe.getCategoria());
            intent.putExtra("recipeTime", recipe.getTiempo());
            intent.putExtra("recipeDifficulty", recipe.getDificultad());
            intent.putExtra("recipeDescription", recipe.getDescripcion());
            intent.putExtra("recipeImageUrl", recipe.getImagenUrl());
            intent.putStringArrayListExtra("recipeIngredients", new ArrayList<>(recipe.getIngredientes()));
            intent.putStringArrayListExtra("recipeSteps", new ArrayList<>(recipe.getPasos()));
            startActivity(intent);
        });
        recetasRecyclerView.setAdapter(recipeAdapter);

        cargarRecetasDesdeFirestore();
    }
    private void cargarRecetasDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recetas").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    recetas.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Recipe receta = new Recipe();
                        receta.setId(doc.getId());
                        receta.setNombre(doc.getString("titulo"));
                        receta.setCategoria(doc.getString("tipo"));
                        receta.setTiempo(doc.getString("tiempo"));
                        receta.setDificultad(doc.getString("dificultad"));
                        receta.setDescripcion(doc.getString("descripcion"));
                        receta.setImagenUrl(doc.getString("imagen"));

                        // INGREDIENTES
                        List<String> ingredientesList = new ArrayList<>();
                        if (doc.contains("ingredientes")) {
                            Map<String, Object> ingMap = (Map<String, Object>) doc.get("ingredientes");
                            for (Object value : ingMap.values()) {
                                Map<String, Object> ing = (Map<String, Object>) value;
                                String nombre = ing.get("nombre") != null ? ing.get("nombre").toString() : "";
                                String cantidad = ing.get("cantidad") != null ? ing.get("cantidad").toString() : "";
                                ingredientesList.add(cantidad + " " + nombre);
                            }
                        }
                        receta.setIngredientes(ingredientesList);

                        // PASOS
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

                        recetas.add(receta);
                    }

                    recipeAdapter.actualizarRecetas(recetas);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar recetas: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void irABusquedaConFiltro(String categoria) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("filtro", categoria);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_buscar) {
            startActivity(new Intent(this, SearchActivity.class));
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
