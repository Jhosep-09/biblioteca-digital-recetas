package com.example.biblioteca_digital.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.biblioteca_digital.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Identificadores / datos de la receta (se obtienen desde Intent)
    private String currentRecipeId;
    private String recipeImageUrl;

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

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Leer extras (si existen)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            currentRecipeId = extras.getString("recipeId"); // asegúrate de mandar este extra
            String recipeName = extras.getString("recipeName");
            String recipeCategory = extras.getString("recipeCategory");
            String recipeTime = extras.getString("recipeTime");
            String recipeDifficulty = extras.getString("recipeDifficulty");
            String recipeDescription = extras.getString("recipeDescription");
            recipeImageUrl = extras.getString("recipeImageUrl"); // URL desde Firebase
            ArrayList<String> ingredientes = extras.getStringArrayList("recipeIngredients");
            ArrayList<String> pasos = extras.getStringArrayList("recipeSteps");

            // Cargar imagen desde URL o drawable por defecto
            if (recipeImageUrl != null && recipeImageUrl.startsWith("http")) {
                Glide.with(this)
                        .load(recipeImageUrl)
                        .placeholder(R.drawable.nofot)
                        .error(R.drawable.nofot)
                        .into(recipeImageView);
                recipeImageView.setTag(recipeImageUrl);
            } else {
                recipeImageView.setImageResource(R.drawable.nofot);
            }

            recipeNameTextView.setText(recipeName != null ? recipeName : "");
            recipeCategoryTextView.setText("Categoría: " + (recipeCategory != null ? recipeCategory : ""));
            recipeTimeTextView.setText("⏱️ " + (recipeTime != null ? recipeTime : ""));
            recipeDifficultyTextView.setText("Dificultad: " + (recipeDifficulty != null ? recipeDifficulty : ""));
            recipeDescriptionTextView.setText(recipeDescription != null ? recipeDescription : "");

            // INGREDIENTES
            if (ingredientes != null && !ingredientes.isEmpty()) {
                SimpleListAdapter ingredientAdapter =
                        new SimpleListAdapter(this, ingredientes, "Ingredientes");
                ingredientsListView.setAdapter(ingredientAdapter);
            }
            android.util.Log.d("RECETA_DETAIL", "INGREDIENTES adapter count=" + ingredientsListView.getCount());
            for (int i=0;i<ingredientsListView.getCount();i++){
                android.util.Log.d("RECETA_DETAIL", "INGREDIENTE["+i+"]=" + ingredientsListView.getItemAtPosition(i));
            }

            // PASOS
            if (pasos != null && !pasos.isEmpty()) {
                SimpleListAdapter stepsAdapter =
                        new SimpleListAdapter(this, pasos, "Pasos");
                stepsListView.setAdapter(stepsAdapter);
            }
            android.util.Log.d("RECETA_DETAIL", "PASOS adapter count=" + stepsListView.getCount());
            for (int i=0;i<stepsListView.getCount();i++){
                android.util.Log.d("RECETA_DETAIL", "PASO["+i+"]=" + stepsListView.getItemAtPosition(i));
            }
        }

        // Comprobar si ya es favorito (solo si hay usuario logueado y recipeId)
        if (auth.getCurrentUser() != null && currentRecipeId != null) {
            String uid = auth.getCurrentUser().getUid();
            db.collection("usuarios").document(uid)
                    .collection("favorites").document(currentRecipeId)
                    .get().addOnSuccessListener(doc -> {
                        if (doc != null && doc.exists()) {
                            isFavorite = true;
                            addFavoriteButton.setText("Quitar de Favoritos");
                        } else {
                            isFavorite = false;
                            addFavoriteButton.setText("Agregar a Favoritos");
                        }
                    }).addOnFailureListener(e -> {
                        // en caso de fallo, dejar la UI en estado por defecto
                        isFavorite = false;
                        addFavoriteButton.setText("Agregar a Favoritos");
                    });
        } else {
            // si no hay usuario o id, mostrar estado por defecto
            addFavoriteButton.setText("Agregar a Favoritos");
        }

        addFavoriteButton.setOnClickListener(v -> toggleFavorite());
        backButton.setOnClickListener(v -> finish());
    }

    private void toggleFavorite() {
        if (auth.getCurrentUser() == null || currentRecipeId == null) {
            Toast.makeText(this, "Debes iniciar sesión para usar favoritos", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        DocumentReference favRef = db.collection("usuarios").document(uid)
                .collection("favorites").document(currentRecipeId);

        if (!isFavorite) {
            // crear favorito (guardamos copia mínima)
            Map<String,Object> data = new HashMap<>();
            data.put("recipeId", currentRecipeId);
            data.put("titulo", recipeNameTextView.getText().toString());
            // preferimos la URL si la tenemos
            String img = recipeImageUrl != null ? recipeImageUrl : (recipeImageView.getTag() != null ? recipeImageView.getTag().toString() : "");
            data.put("imagen", img);
            data.put("savedAt", FieldValue.serverTimestamp());

            favRef.set(data).addOnSuccessListener(aVoid -> {
                isFavorite = true;
                addFavoriteButton.setText("Quitar de Favoritos");
                Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Error guardando favorito: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );

        } else {
            // eliminar favorito
            favRef.delete().addOnSuccessListener(aVoid -> {
                isFavorite = false;
                addFavoriteButton.setText("Agregar a Favoritos");
                Toast.makeText(this, "Removido de favoritos", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Error removiendo favorito: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        }
    }

    // Adapter para mostrar listas simples
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