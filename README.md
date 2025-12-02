# Biblioteca Digital de Recetas

INTEGRANTES:
- Simon Gonzales Ronaldo Jhosep
- Estrada Rivera Jamil
- Dueñas Loyola Yhozira Milagros
- Huancaya Recines Ericsson
- Ramirez Masgo Nilton Daniel

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

---

✨ Avances del Proyecto Android con Firebase ✨
📌 Integración Firebase
- Creación y configuración inicial de Firebase
- Diseño de modelo de datos y diagrama de clases
- Conexión establecida con el proyecto Android
📌 MainActivity
- Migración de estático ➝ funcional
- Menú principal con recetas dinámicas obtenidas desde Firebase
📌 Recipe & RecipeDetail
- Conversión de pantallas estáticas ➝ funcionales
- Implementación de RecyclerView con recetas de Firebase
- Visualización en tiempo real de recetas existentes en la base de datos
📌 Autenticación de usuarios
- Login falso reemplazado por Firebase Auth
- Registro de usuarios vinculado a Firebase
- Creación de cuentas y almacenamiento automático en la base de datos
📌 Gestión de imágenes
- Se agregó imagen “nofoto” como fallback
- Aparece cuando falla la llamada a la URL o el campo está vacío
📌 Mejoras de diseño
- Optimización de estilos visuales
- Ajustes en MainActivity y detalle de receta para una mejor experiencia


**Última actualización:** 30 de Noviembre 2025
