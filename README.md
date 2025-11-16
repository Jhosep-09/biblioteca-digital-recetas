# Biblioteca Digital de Recetas

Una aplicación móvil Android para descubrir, filtrar y guardar recetas favoritas.

## 📱 Estado Actual: Hito 1

**Funcionalidades implementadas:**

### ✅ Autenticación
- LoginActivity - Inicio de sesión
- RegisterActivity - Registro de nuevos usuarios
- Validación básica de campos

### ✅ Búsqueda y Filtrado
- SearchActivity con RecyclerView en grid
- Búsqueda en tiempo real por nombre
- Filtros por categoría: Vegetariana, Rápida, Postres
- 10 recetas de ejemplo incluidas

### ✅ Detalles de Receta
- RecipeDetailActivity con información completa
- Imagen de receta
- Ingredientes listados
- Pasos numerados de preparación
- Opción para marcar/desmarcar como favorita

### ✅ Recetas Favoritas
- FavoritesActivity con lista de favoritas
- 3 recetas de ejemplo como favoritas
- Navegación a detalles desde favoritos

### ✅ Navegación
- Menu toolbar con opciones (Buscar, Favoritos, Perfil, Cerrar Sesión)
- MainActivity con bienvenida y accesos rápidos
- Navegación fluida entre pantallas

### ✅ Bienvenida Mejorada
- 4 categorías clickeables (Vegetariana, Rápida, Postres, Todas)
- 3 recetas destacadas
- Diseño limpio sin redundancias

---

## 🛠️ Tecnologías Usadas

- **Lenguaje:** Java
- **Framework:** Android Studio
- **Componentes:** 
  - RecyclerView (mostrar recetas)
  - CardView (items)
  - Bottom Navigation (cuando esté)
  - Toolbar (menu)

---

## 📦 Requisitos

- Android API 24+
- Java 11+
- Android Studio

## 📊 Estructura de Carpetas
```
app/
├── java/com/example/biblioteca_digital/
│   ├── Activities/
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   ├── SearchActivity.java
│   │   ├── RecipeDetailActivity.java
│   │   └── FavoritesActivity.java
│   ├── Adapters/
│   │   └── RecipeAdapter.java
│   ├── Models/
│   │   └── Recipe.java
│   └── MainActivity.java
│
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   ├── activity_login.xml
    │   ├── activity_register.xml
    │   ├── activity_search.xml
    │   ├── activity_recipe_detail.xml
    │   ├── activity_favorites.xml
    │   └── item_recipe.xml
    ├── menu/
    │   └── menu_toolbar.xml
    └── values/
        ├── strings.xml
        └── colors.xml
```

---

## 🔄 Flujo de la Aplicación
```
LoginActivity / RegisterActivity
         ↓
    MainActivity (Bienvenida)
    ↙         ↓         ↘
Búsqueda  Favoritos  Perfil
    ↓
RecipeDetailActivity
    ↓
Marcar como favorita
```

---

## 📝 Recetas de Ejemplo

La app incluye 10 recetas:
- Pasta Carbonara (Rápida)
- Ensalada Vegetariana (Vegetariana)
- Brownies de Chocolate (Postres)
- Arroz con Verduras (Vegetariana)
- Pollo al Limón (Rápida)
- Tarta de Fresa (Postres)
- Sopa de Verduras (Vegetariana)
- Tacos Rápidos (Rápida)
- Cheesecake (Postres)
- Smoothie Tropical (Rápida)

---

## 🔐 Credenciales de Prueba
```
Email: usuario@ejemplo.com
Contraseña: 123456
```

O puedes crear una nueva cuenta con el formulario de registro.

---

## 🎯 Próximos Pasos (Hito 2)

- [ ] Integración con Firebase Authentication
- [ ] Base de datos en Firebase Realtime Database
- [ ] Guardar favoritas en la nube
- [ ] Cargar imágenes desde Firebase Cloud Storage
- [ ] Sincronización de datos en múltiples dispositivos

---

## ⚠️ Limitaciones Actuales

- ❌ Los favoritas NO se guardan (solo en sesión)
- ❌ Las imágenes son placeholders
- ❌ Sin persistencia de datos
- ⚠️ Datos hardcodeados (sin base de datos)

*Estos se implementarán en Hito 2 con Firebase*


**Última actualización:** 16 de Noviembre 2025
