package com.example.biblioteca_digital.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteca_digital.Adapters.RecipeAdapter;
import com.example.biblioteca_digital.Models.Recipe;
import com.example.biblioteca_digital.R;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private RecipeAdapter adapter;
    private TextView emptyStateTextView;
    private List<Recipe> recetasFavoritas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recetasFavoritas = obtenerRecetasFavoritasDeEjemplo();

        adapter = new RecipeAdapter(recetasFavoritas, this::abrirDetallesReceta);
        favoritesRecyclerView.setAdapter(adapter);

        if (recetasFavoritas.isEmpty()) {
            emptyStateTextView.setVisibility(TextView.VISIBLE);
            favoritesRecyclerView.setVisibility(RecyclerView.GONE);
        } else {
            emptyStateTextView.setVisibility(TextView.GONE);
            favoritesRecyclerView.setVisibility(RecyclerView.VISIBLE);
        }
    }

    private void abrirDetallesReceta(Recipe recipe) {
        Intent intent = new Intent(FavoritesActivity.this, RecipeDetailActivity.class);
        intent.putExtra("recipeId", recipe.getId());
        intent.putExtra("recipeName", recipe.getNombre());
        intent.putExtra("recipeCategory", recipe.getCategoria());
        intent.putExtra("recipeTime", recipe.getTiempo());
        intent.putExtra("recipeDifficulty", recipe.getDificultad());
        intent.putExtra("recipeImage", recipe.getImagenResId());
        intent.putExtra("recipeDescription", recipe.getDescripcion());
        intent.putStringArrayListExtra("recipeIngredients", new ArrayList<>(recipe.getIngredientes()));
        intent.putStringArrayListExtra("recipeSteps", new ArrayList<>(recipe.getPasos()));
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private List<Recipe> obtenerRecetasFavoritasDeEjemplo() {
        List<Recipe> recetas = new ArrayList<>();

        Recipe r1 = new Recipe(1, "Pasta Carbonara", "Rápida", "20 min", "Fácil",
                android.R.drawable.ic_menu_gallery, "Deliciosa pasta a la italiana");
        r1.agregarIngrediente("200g de pasta");
        r1.agregarIngrediente("100g de panceta");
        r1.agregarIngrediente("3 huevos");
        r1.agregarIngrediente("Queso parmesano");
        r1.agregarPaso("Hervir agua con sal");
        r1.agregarPaso("Cocinar la pasta");
        r1.agregarPaso("Freír la panceta");
        r1.agregarPaso("Mezclar ingredientes");
        r1.setEsFavorita(true);
        recetas.add(r1);

        Recipe r2 = new Recipe(3, "Brownies de Chocolate", "Postres", "45 min", "Medio",
                android.R.drawable.ic_menu_gallery, "Brownies deliciosos y esponjosos");
        r2.agregarIngrediente("200g de chocolate");
        r2.agregarIngrediente("150g de mantequilla");
        r2.agregarIngrediente("200g de harina");
        r2.agregarIngrediente("4 huevos");
        r2.agregarPaso("Derretir chocolate y mantequilla");
        r2.agregarPaso("Mezclar huevos");
        r2.agregarPaso("Añadir harina");
        r2.agregarPaso("Hornear 30 minutos");
        r2.setEsFavorita(true);
        recetas.add(r2);

        Recipe r3 = new Recipe(5, "Pollo al Limón", "Rápida", "30 min", "Medio",
                android.R.drawable.ic_menu_gallery, "Pollo jugoso y sabroso");
        r3.agregarIngrediente("600g de pechuga de pollo");
        r3.agregarIngrediente("3 limones");
        r3.agregarIngrediente("Ajo");
        r3.agregarIngrediente("Aceite de oliva");
        r3.agregarPaso("Preparar marinada");
        r3.agregarPaso("Dejar reposar el pollo");
        r3.agregarPaso("Cocinar en sartén");
        r3.agregarPaso("Servir caliente");
        r3.setEsFavorita(true);
        recetas.add(r3);

        return recetas;
    }
}