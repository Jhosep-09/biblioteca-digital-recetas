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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentSnapshot;

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
        ArrayList<String> ingredientesExtras = null;
        ArrayList<String> pasosExtras = null;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            currentRecipeId = extras.getString("recipeId"); // id del documento en Firestore
            String recipeName = extras.getString("recipeName");
            String recipeCategory = extras.getString("recipeCategory");
            String recipeTime = extras.getString("recipeTime");
            String recipeDifficulty = extras.getString("recipeDifficulty");
            String recipeDescription = extras.getString("recipeDescription");
            recipeImageUrl = extras.getString("recipeImageUrl"); // URL desde Firebase
            ingredientesExtras = extras.getStringArrayList("recipeIngredients");
            pasosExtras = extras.getStringArrayList("recipeSteps");

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
        }

        // 1) Si VINIERON ingredientes/pasos por extras, los mostramos
        if (ingredientesExtras != null && !ingredientesExtras.isEmpty()) {
            SimpleListAdapter ingredientAdapter =
                    new SimpleListAdapter(this, ingredientesExtras, "Ingredientes");
            ingredientsListView.setAdapter(ingredientAdapter);
            setListViewHeightBasedOnChildren(ingredientsListView);
        }

        if (pasosExtras != null && !pasosExtras.isEmpty()) {
            SimpleListAdapter stepsAdapter =
                    new SimpleListAdapter(this, pasosExtras, "Pasos");
            stepsListView.setAdapter(stepsAdapter);
            setListViewHeightBasedOnChildren(stepsListView);
        }

        // 2) Si NO vinieron o están vacíos, los cargamos desde las SUBCOLECCIONES
        if ((ingredientesExtras == null || ingredientesExtras.isEmpty()) && currentRecipeId != null) {
            cargarIngredientesDesdeFirestore();
        }
        if ((pasosExtras == null || pasosExtras.isEmpty()) && currentRecipeId != null) {
            cargarPasosDesdeFirestore();
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

    /**
     * Lee la subcolección ingredientes de recetas/{id}/ingredientes
     */
    private void cargarIngredientesDesdeFirestore() {
        if (currentRecipeId == null) return;

        db.collection("recetas")
                .document(currentRecipeId)
                .collection("ingredientes") // nombre de la subcolección
                .get()
                .addOnSuccessListener(query -> {
                    ArrayList<String> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String nombre = doc.getString("nombre");
                        String cantidad = doc.getString("cantidad");

                        if (nombre == null) nombre = "";
                        if (cantidad == null) cantidad = "";

                        String item = (cantidad.isEmpty() ? "" : cantidad + " ") + nombre;
                        lista.add(item);
                    }

                    android.util.Log.d("RECETA_DETAIL", "Ingredientes subcolección count=" + lista.size());

                    if (!lista.isEmpty()) {
                        SimpleListAdapter ingredientAdapter =
                                new SimpleListAdapter(this, lista, "Ingredientes");
                        ingredientsListView.setAdapter(ingredientAdapter);
                        setListViewHeightBasedOnChildren(ingredientsListView);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("RECETA_DETAIL", "Error cargando ingredientes subcolección", e);
                });
    }

    /**
     * Lee la subcolección pasos de recetas/{id}/pasos
     */
    private void cargarPasosDesdeFirestore() {
        if (currentRecipeId == null) return;

        db.collection("recetas")
                .document(currentRecipeId)
                .collection("pasos")
                .orderBy("numero")
                .get()
                .addOnSuccessListener(query -> {
                    ArrayList<String> descripciones = new ArrayList<>();
                    ArrayList<String> imagenes = new ArrayList<>();

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String desc = doc.getString("descripcion");
                        String img = doc.getString("imagen");

                        android.util.Log.d(
                                "PASO_DEBUG",
                                "docId=" + doc.getId()
                                        + " numero=" + doc.get("numero")
                                        + " desc=" + desc
                                        + " imgRAW=[" + img + "]"
                        );

                        // 🔥 LIMPIAR ESPACIOS (muy probable que aquí esté el problema)
                        if (img != null) {
                            img = img.trim();
                        }

                        if (desc == null) desc = "";
                        descripciones.add(desc);
                        imagenes.add(img);
                    }

                    android.util.Log.d("RECETA_DETAIL",
                            "Pasos subcolección count=" + descripciones.size());

                    if (!descripciones.isEmpty()) {
                        StepsListAdapter stepsAdapter =
                                new StepsListAdapter(this, descripciones, imagenes);
                        stepsListView.setAdapter(stepsAdapter);
                        setListViewHeightBasedOnChildren(stepsListView);
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("RECETA_DETAIL", "Error cargando pasos subcolección", e);
                });
    }



    // Ajustar altura de ListView dentro del ScrollView
    private void setListViewHeightBasedOnChildren(ListView listView) {
        android.widget.ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            android.view.View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(
                    android.view.View.MeasureSpec.makeMeasureSpec(listView.getWidth(), android.view.View.MeasureSpec.AT_MOST),
                    android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += listItem.getMeasuredHeight();
        }
        int dividerHeight = listView.getDividerHeight() * (listAdapter.getCount() - 1);
        android.view.ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + dividerHeight + listView.getPaddingTop() + listView.getPaddingBottom();
        listView.setLayoutParams(params);
        listView.requestLayout();
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
            Map<String, Object> data = new HashMap<>();
            data.put("recipeId", currentRecipeId);
            data.put("titulo", recipeNameTextView.getText().toString());
            // preferimos la URL si la tenemos
            String img = recipeImageUrl != null
                    ? recipeImageUrl
                    : (recipeImageView.getTag() != null ? recipeImageView.getTag().toString() : "");
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
