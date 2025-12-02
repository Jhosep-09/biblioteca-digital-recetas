package com.example.biblioteca_digital.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.biblioteca_digital.R;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeNameTextView;
    private TextView recipeCategoryTextView;
    private TextView recipeTimeTextView;
    private TextView recipeDifficultyTextView;
    private TextView recipeDescriptionTextView;
    private ListView ingredientsListView;
    private ListView stepsListView;

    private ImageView btnFavorite;  // ← AHORA usamos este
    private Button backButton;

    private boolean isFavorite = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Obtener vistas
        recipeImageView = findViewById(R.id.recipeImageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        recipeCategoryTextView = findViewById(R.id.recipeCategoryTextView);
        recipeTimeTextView = findViewById(R.id.recipeTimeTextView);
        recipeDifficultyTextView = findViewById(R.id.recipeDifficultyTextView);
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView);
        ingredientsListView = findViewById(R.id.ingredientsListView);
        stepsListView = findViewById(R.id.stepsListView);
        backButton = findViewById(R.id.backButton);

        btnFavorite = findViewById(R.id.btnFavorite); // ← Nuevo botón favorito

        // Recibir datos del intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String recipeName = extras.getString("recipeName");
            String recipeCategory = extras.getString("recipeCategory");
            String recipeTime = extras.getString("recipeTime");
            String recipeDifficulty = extras.getString("recipeDifficulty");
            String recipeDescription = extras.getString("recipeDescription");
            String recipeImageUrl = extras.getString("recipeImageUrl");
            ArrayList<String> ingredients = extras.getStringArrayList("recipeIngredients");
            ArrayList<String> steps = extras.getStringArrayList("recipeSteps");

            // Cargar imagen
            if (recipeImageUrl != null && recipeImageUrl.startsWith("http")) {
                Glide.with(this)
                        .load(recipeImageUrl)
                        .placeholder(R.drawable.nofot)
                        .error(R.drawable.nofot)
                        .into(recipeImageView);
            } else {
                recipeImageView.setImageResource(R.drawable.nofot);
            }

            // Mostrar textos
            recipeNameTextView.setText(recipeName);
            recipeCategoryTextView.setText("Categoría: " + recipeCategory);
            recipeTimeTextView.setText("⏱️ " + recipeTime);
            recipeDifficultyTextView.setText("Dificultad: " + recipeDifficulty);
            recipeDescriptionTextView.setText(recipeDescription);

            // LISTA DE INGREDIENTES
            if (ingredients != null && !ingredients.isEmpty()) {
                SimpleListAdapter adapterIng =
                        new SimpleListAdapter(this, ingredients, "Ingredientes");
                ingredientsListView.setAdapter(adapterIng);
            }

            // LISTA DE PASOS
            if (steps != null && !steps.isEmpty()) {
                SimpleListAdapter adapterSteps =
                        new SimpleListAdapter(this, steps, "Pasos");
                stepsListView.setAdapter(adapterSteps);
            }
        }

        // ❗ EVENTO DE FAVORITOS
        btnFavorite.setOnClickListener(v -> toggleFavorite());

        // Botón volver
        backButton.setOnClickListener(v -> finish());
    }

    // Cambia entre corazón vacío ❤️ y corazón lleno 🤍
    private void toggleFavorite() {
        isFavorite = !isFavorite;

        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled); // icono lleno
            Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border); // icono vacío
            Toast.makeText(this, "Removido de favoritos", Toast.LENGTH_SHORT).show();
        }
    }


    // Adaptador simple para las listas
    public static class SimpleListAdapter extends android.widget.ArrayAdapter<String> {
        private final ArrayList<String> items;
        private final String type;

        public SimpleListAdapter(android.content.Context context, ArrayList<String> items, String type) {
            super(context, android.R.layout.simple_list_item_1, items);
            this.items = items;
            this.type = type;
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = android.view.LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView text = convertView.findViewById(android.R.id.text1);

            if (type.equals("Pasos")) {
                text.setText((position + 1) + ". " + items.get(position));
            } else {
                text.setText("• " + items.get(position));
            }

            return convertView;
        }
    }
}
