package com.example.biblioteca_digital.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
    private Button addFavoriteButton;
    private Button backButton;
    private boolean isFavorite = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        recipeImageView = findViewById(R.id.recipeImageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        recipeCategoryTextView = findViewById(R.id.recipeCategoryTextView);
        recipeTimeTextView = findViewById(R.id.recipeTimeTextView);
        recipeDifficultyTextView = findViewById(R.id.recipeDifficultyTextView);
        recipeDescriptionTextView = findViewById(R.id.recipeDescriptionTextView);
        ingredientsListView = findViewById(R.id.ingredientsListView);
        stepsListView = findViewById(R.id.stepsListView);
        addFavoriteButton = findViewById(R.id.addFavoriteButton);
        backButton = findViewById(R.id.backButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int recipeId = extras.getInt("recipeId");
            String recipeName = extras.getString("recipeName");
            String recipeCategory = extras.getString("recipeCategory");
            String recipeTime = extras.getString("recipeTime");
            String recipeDifficulty = extras.getString("recipeDifficulty");
            int recipeImage = extras.getInt("recipeImage");
            String recipeDescription = extras.getString("recipeDescription");
            ArrayList<String> ingredients = extras.getStringArrayList("recipeIngredients");
            ArrayList<String> steps = extras.getStringArrayList("recipeSteps");

            recipeImageView.setImageResource(recipeImage);
            recipeNameTextView.setText(recipeName);
            recipeCategoryTextView.setText("Categoría: " + recipeCategory);
            recipeTimeTextView.setText("⏱️ " + recipeTime);
            recipeDifficultyTextView.setText("Dificultad: " + recipeDifficulty);
            recipeDescriptionTextView.setText(recipeDescription);

            if (ingredients != null && !ingredients.isEmpty()) {
                SimpleListAdapter ingredientAdapter = new SimpleListAdapter(this, ingredients, "Ingredientes");
                ingredientsListView.setAdapter(ingredientAdapter);
            }

            if (steps != null && !steps.isEmpty()) {
                SimpleListAdapter stepsAdapter = new SimpleListAdapter(this, steps, "Pasos");
                stepsListView.setAdapter(stepsAdapter);
            }
        }

        addFavoriteButton.setOnClickListener(v -> toggleFavorite());
        backButton.setOnClickListener(v -> finish());
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;

        if (isFavorite) {
            addFavoriteButton.setText("❤️ Quitar de Favoritos");
            addFavoriteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
        } else {
            addFavoriteButton.setText("🤍 Agregar a Favoritos");
            addFavoriteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            Toast.makeText(this, "Removido de favoritos", Toast.LENGTH_SHORT).show();
        }
    }

    public static class SimpleListAdapter extends android.widget.ArrayAdapter<String> {
        private ArrayList<String> items;
        private String type;

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

            TextView textView = convertView.findViewById(android.R.id.text1);

            if (type.equals("Pasos")) {
                textView.setText((position + 1) + ". " + items.get(position));
            } else {
                textView.setText("• " + items.get(position));
            }

            return convertView;
        }
    }
}