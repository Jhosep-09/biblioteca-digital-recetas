package com.example.biblioteca_digital;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.biblioteca_digital.Activities.LoginActivity;
import com.example.biblioteca_digital.Activities.SearchActivity;
import com.example.biblioteca_digital.Activities.FavoritesActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnCategoryVegetariana;
    private LinearLayout btnCategoryRapida;
    private LinearLayout btnCategoryPostres;
    private LinearLayout btnCategoryTodas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializar botones de categorías
        btnCategoryVegetariana = findViewById(R.id.btnCategoryVegetariana);
        btnCategoryRapida = findViewById(R.id.btnCategoryRapida);
        btnCategoryPostres = findViewById(R.id.btnCategoryPostres);
        btnCategoryTodas = findViewById(R.id.btnCategoryTodas);

        // Click listeners para categorías
        btnCategoryVegetariana.setOnClickListener(v -> irABusquedaConFiltro("Vegetariana"));
        btnCategoryRapida.setOnClickListener(v -> irABusquedaConFiltro("Rápida"));
        btnCategoryPostres.setOnClickListener(v -> irABusquedaConFiltro("Postres"));
        btnCategoryTodas.setOnClickListener(v -> irABusquedaConFiltro("Todas"));
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
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.menu_favoritos) {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.menu_perfil) {
            Toast.makeText(this, "Perfil: Usuarios Demo", Toast.LENGTH_SHORT).show();
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