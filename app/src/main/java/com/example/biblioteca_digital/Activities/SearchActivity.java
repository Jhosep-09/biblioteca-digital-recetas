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
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RadioGroup filterRadioGroup;
    private RadioButton filterAll;
    private RadioButton filterVegetarian;
    private RadioButton filterFast;
    private RadioButton filterDesserts;
    private RecyclerView recipesRecyclerView;
    private RecipeAdapter adapter;
    private List<Recipe> todasLasRecetas;
    private List<Recipe> recetasFiltradas;

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

        todasLasRecetas = obtenerRecetasDeEjemplo();
        recetasFiltradas = new ArrayList<>(todasLasRecetas);

        adapter = new RecipeAdapter(recetasFiltradas, this::abrirDetallesReceta);
        recipesRecyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarRecetas(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterRadioGroup.setOnCheckedChangeListener((group, checkedId) -> aplicarFiltros());
    }

    private void filtrarRecetas(String termino) {
        String filterCategoria = obtenerCategoriaSelecionada();
        recetasFiltradas.clear();

        for (Recipe recipe : todasLasRecetas) {
            boolean coincideNombre = recipe.getNombre()
                    .toLowerCase()
                    .contains(termino.toLowerCase());

            boolean coincideCategoria = filterCategoria.equals("Todas") ||
                    recipe.getCategoria().equals(filterCategoria);

            if (coincideNombre && coincideCategoria) {
                recetasFiltradas.add(recipe);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void aplicarFiltros() {
        String termino = searchEditText.getText().toString();
        filtrarRecetas(termino);
    }

    private String obtenerCategoriaSelecionada() {
        int selectedId = filterRadioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.filterVegetarian) {
            return "Vegetariana";
        } else if (selectedId == R.id.filterFast) {
            return "Rápida";
        } else if (selectedId == R.id.filterDesserts) {
            return "Postres";
        }

        return "Todas";
    }

    private void abrirDetallesReceta(Recipe recipe) {
        Intent intent = new Intent(SearchActivity.this, RecipeDetailActivity.class);
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

    private List<Recipe> obtenerRecetasDeEjemplo() {
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
        recetas.add(r1);

        Recipe r2 = new Recipe(2, "Ensalada Vegetariana", "Vegetariana", "10 min", "Fácil",
                android.R.drawable.ic_menu_gallery, "Ensalada fresca y saludable");
        r2.agregarIngrediente("Lechuga");
        r2.agregarIngrediente("Tomate");
        r2.agregarIngrediente("Pepino");
        r2.agregarIngrediente("Cebolla");
        r2.agregarPaso("Lavar verduras");
        r2.agregarPaso("Cortar en trozos");
        r2.agregarPaso("Mezclar en un tazón");
        r2.agregarPaso("Añadir aderezo");
        recetas.add(r2);

        Recipe r3 = new Recipe(3, "Brownies de Chocolate", "Postres", "45 min", "Medio",
                android.R.drawable.ic_menu_gallery, "Brownies deliciosos y esponjosos");
        r3.agregarIngrediente("200g de chocolate");
        r3.agregarIngrediente("150g de mantequilla");
        r3.agregarIngrediente("200g de harina");
        r3.agregarIngrediente("4 huevos");
        r3.agregarPaso("Derretir chocolate y mantequilla");
        r3.agregarPaso("Mezclar huevos");
        r3.agregarPaso("Añadir harina");
        r3.agregarPaso("Hornear 30 minutos");
        recetas.add(r3);

        Recipe r4 = new Recipe(4, "Arroz con Verduras", "Vegetariana", "25 min", "Fácil",
                android.R.drawable.ic_menu_gallery, "Arroz nutritivo y delicioso");
        r4.agregarIngrediente("300g de arroz");
        r4.agregarIngrediente("Cebolla");
        r4.agregarIngrediente("Zanahoria");
        r4.agregarIngrediente("Chícharo");
        r4.agregarPaso("Cocinar arroz");
        r4.agregarPaso("Picar verduras");
        r4.agregarPaso("Saltear verduras");
        r4.agregarPaso("Mezclar con arroz");
        recetas.add(r4);

        Recipe r5 = new Recipe(5, "Pollo al Limón", "Rápida", "30 min", "Medio",
                android.R.drawable.ic_menu_gallery, "Pollo jugoso y sabroso");
        r5.agregarIngrediente("600g de pechuga de pollo");
        r5.agregarIngrediente("3 limones");
        r5.agregarIngrediente("Ajo");
        r5.agregarIngrediente("Aceite de oliva");
        r5.agregarPaso("Preparar marinada");
        r5.agregarPaso("Dejar reposar el pollo");
        r5.agregarPaso("Cocinar en sartén");
        r5.agregarPaso("Servir caliente");
        recetas.add(r5);

        Recipe r6 = new Recipe(6, "Tarta de Fresa", "Postres", "60 min", "Difícil",
                android.R.drawable.ic_menu_gallery, "Postre elegante y sabroso");
        r6.agregarIngrediente("300g de fresas");
        r6.agregarIngrediente("250g de harina");
        r6.agregarIngrediente("200g de azúcar");
        r6.agregarIngrediente("Crema pastelera");
        r6.agregarPaso("Preparar masa");
        r6.agregarPaso("Hornear base");
        r6.agregarPaso("Preparar crema");
        r6.agregarPaso("Decorar con fresas");
        recetas.add(r6);

        Recipe r7 = new Recipe(7, "Sopa de Verduras", "Vegetariana", "35 min", "Fácil",
                android.R.drawable.ic_menu_gallery, "Sopa casera y nutritiva");
        r7.agregarIngrediente("Zanahoria");
        r7.agregarIngrediente("Papas");
        r7.agregarIngrediente("Cebolla");
        r7.agregarIngrediente("Caldo de verduras");
        r7.agregarPaso("Picar verduras");
        r7.agregarPaso("Hervir agua");
        r7.agregarPaso("Cocinar verduras");
        r7.agregarPaso("Salpimentar al gusto");
        recetas.add(r7);

        Recipe r8 = new Recipe(8, "Tacos Rápidos", "Rápida", "15 min", "Fácil",
                android.R.drawable.ic_menu_gallery, "Tacos listos en minutos");
        r8.agregarIngrediente("Tortillas");
        r8.agregarIngrediente("Pollo deshebrado");
        r8.agregarIngrediente("Lechuga");
        r8.agregarIngrediente("Salsa");
        r8.agregarPaso("Calentar tortillas");
        r8.agregarPaso("Rellenar tacos");
        r8.agregarPaso("Agregar lechuga");
        r8.agregarPaso("Servir con salsa");
        recetas.add(r8);

        Recipe r9 = new Recipe(9, "Cheesecake", "Postres", "90 min", "Difícil",
                android.R.drawable.ic_menu_gallery, "Postre cremoso y delicioso");
        r9.agregarIngrediente("500g de queso crema");
        r9.agregarIngrediente("Galletas");
        r9.agregarIngrediente("Huevos");
        r9.agregarIngrediente("Azúcar");
        r9.agregarPaso("Preparar base");
        r9.agregarPaso("Mezclar queso");
        r9.agregarPaso("Hornear");
        r9.agregarPaso("Enfriar");
        recetas.add(r9);

        Recipe r10 = new Recipe(10, "Smoothie Tropical", "Rápida", "5 min", "Fácil",
                android.R.drawable.ic_menu_gallery, "Bebida refrescante");
        r10.agregarIngrediente("1 plátano");
        r10.agregarIngrediente("1 mango");
        r10.agregarIngrediente("200ml leche");
        r10.agregarIngrediente("Miel");
        r10.agregarPaso("Pelar frutas");
        r10.agregarPaso("Agregar a licuadora");
        r10.agregarPaso("Licuar bien");
        r10.agregarPaso("Servir frío");
        recetas.add(r10);

        return recetas;
    }
}