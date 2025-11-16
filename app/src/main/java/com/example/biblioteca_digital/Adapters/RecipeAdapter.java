package com.example.biblioteca_digital.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biblioteca_digital.Models.Recipe;
import com.example.biblioteca_digital.R;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recetas;
    private OnRecipeClickListener onRecipeClickListener;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(List<Recipe> recetas, OnRecipeClickListener onRecipeClickListener) {
        this.recetas = recetas;
        this.onRecipeClickListener = onRecipeClickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recetas.get(position);
        holder.bind(recipe, onRecipeClickListener);
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagenReceta;
        private TextView nombreReceta;
        private TextView categoriaReceta;
        private TextView tiempoReceta;
        private TextView dificultadReceta;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenReceta = itemView.findViewById(R.id.imagenReceta);
            nombreReceta = itemView.findViewById(R.id.nombreReceta);
            categoriaReceta = itemView.findViewById(R.id.categoriaReceta);
            tiempoReceta = itemView.findViewById(R.id.tiempoReceta);
            dificultadReceta = itemView.findViewById(R.id.dificultadReceta);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Recipe recipe, OnRecipeClickListener listener) {
            imagenReceta.setImageResource(recipe.getImagenResId());
            nombreReceta.setText(recipe.getNombre());
            categoriaReceta.setText(recipe.getCategoria());
            tiempoReceta.setText("⏱️ " + recipe.getTiempo());
            dificultadReceta.setText("Dificultad: " + recipe.getDificultad());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRecipeClick(recipe);
                }
            });
        }
    }

    public void actualizarRecetas(List<Recipe> nuevasRecetas) {
        this.recetas = nuevasRecetas;
        notifyDataSetChanged();
    }

    public void filtrarRecetas(List<Recipe> recetasFiltradas) {
        this.recetas = recetasFiltradas;
        notifyDataSetChanged();
    }
}