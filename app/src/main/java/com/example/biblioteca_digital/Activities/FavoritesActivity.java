package com.example.biblioteca_digital.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteca_digital.Adapters.RecipeAdapter;
import com.example.biblioteca_digital.Models.Recipe;
import com.example.biblioteca_digital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private RecipeAdapter adapter;
    private TextView emptyStateTextView;
    private List<Recipe> recetasFavoritas;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recetasFavoritas = new ArrayList<>();
        adapter = new RecipeAdapter(recetasFavoritas, this::abrirDetallesReceta);
        favoritesRecyclerView.setAdapter(adapter);

        cargarFavoritosDesdeFirestore();
    }

    private void abrirDetallesReceta(Recipe recipe) {
        Intent intent = new Intent(FavoritesActivity.this, RecipeDetailActivity.class);

        // Pasar recipeId y resto de extras (RecipeDetailActivity usa recipeId para favoritos)
        intent.putExtra("recipeId", recipe.getId());
        intent.putExtra("recipeName", recipe.getNombre());
        intent.putExtra("recipeCategory", recipe.getCategoria());
        intent.putExtra("recipeTime", recipe.getTiempo());
        intent.putExtra("recipeDifficulty", recipe.getDificultad());
        intent.putExtra("recipeDescription", recipe.getDescripcion());
        intent.putExtra("recipeImageUrl", recipe.getImagenUrl());
        intent.putStringArrayListExtra("recipeIngredients", new ArrayList<>(recipe.getIngredientes()));
        intent.putStringArrayListExtra("recipeSteps", new ArrayList<>(recipe.getPasos()));

        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void cargarFavoritosDesdeFirestore() {
        if (auth.getCurrentUser() == null) {
            emptyStateTextView.setText("Inicia sesión para ver tus favoritos");
            emptyStateTextView.setVisibility(TextView.VISIBLE);
            favoritesRecyclerView.setVisibility(RecyclerView.GONE);
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(uid).collection("favorites")
                .get()
                .addOnSuccessListener(query -> {
                    recetasFavoritas.clear();

                    List<String> recipeIdsToFetch = new ArrayList<>();
                    List<DocumentSnapshot> favoriteDocs = new ArrayList<>();

                    for (QueryDocumentSnapshot favDoc : query) {
                        favoriteDocs.add(favDoc);
                        String recipeId = favDoc.getString("recipeId");
                        if (recipeId != null) recipeIdsToFetch.add(recipeId);
                    }

                    if (recipeIdsToFetch.isEmpty()) {
                        // no hay favoritos
                        emptyStateTextView.setText("No tienes favoritos aún");
                        emptyStateTextView.setVisibility(TextView.VISIBLE);
                        favoritesRecyclerView.setVisibility(RecyclerView.GONE);
                        return;
                    }

                    // Por cada recipeId: obtener documento de recetas y construir Recipe
                    for (DocumentSnapshot favDoc : favoriteDocs) {
                        String recipeId = favDoc.getString("recipeId");
                        if (recipeId == null) continue;

                        // Intenta obtener la receta completa desde "recetas/{id}"
                        db.collection("recetas").document(recipeId).get()
                                .addOnSuccessListener(doc -> {
                                    if (doc != null && doc.exists()) {
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
                                                ingredientesList.add((cantidad != null && !cantidad.isEmpty() ? cantidad + " " : "") + nombre);
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

                                        receta.setEsFavorita(true);
                                        recetasFavoritas.add(receta);
                                        adapter.notifyDataSetChanged();

                                        emptyStateTextView.setVisibility(recetasFavoritas.isEmpty() ? TextView.VISIBLE : TextView.GONE);
                                        favoritesRecyclerView.setVisibility(recetasFavoritas.isEmpty() ? RecyclerView.GONE : RecyclerView.VISIBLE);
                                    } else {
                                        // Si la receta no existe (posible inconsistencia), podemos ignorarla o eliminar el favorito.
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error cargando receta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error cargando favoritos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}