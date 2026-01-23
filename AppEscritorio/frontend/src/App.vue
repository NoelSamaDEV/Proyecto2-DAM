<script setup>
import { RouterLink, RouterView } from 'vue-router'
// Importamos tu logo local
import logo from '@/assets/logo.png'
</script>

<template>
  <div class="app-layout">
    <aside class="sidebar">
      <div class="logo-area">
        <img :src="logo" alt="Logo" class="logo-img"/>
        <h2>FOODNOW</h2>
      </div>

      <nav>
        <RouterLink to="/" class="nav-btn">MESAS</RouterLink>
        <RouterLink to="/notificaciones" class="nav-btn">NOTIFICACIONES</RouterLink>
      </nav>
    </aside>

    <main class="content-area">
      <RouterView />
    </main>
  </div>
</template>

<style>
/* Reset básico */
body { margin: 0; padding: 0; font-family: 'Arial', sans-serif; background-color: #f4f4f4; }

.app-layout {
  display: flex;
  height: 100vh;
  width: 100vw;
}

/* --- ESTILOS DE LA BARRA LATERAL --- */
.sidebar {
  width: 250px;
  background-color: #223E2A; /* Verde FoodNow */
  color: white;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-shadow: 2px 0 5px rgba(0,0,0,0.2);
}

.logo-area {
  text-align: center;
  margin-bottom: 40px;
  border-bottom: 1px solid #444;
  padding-bottom: 20px;
}

/* --- LOGO MUCHO MÁS GRANDE --- */
.logo-img { 
  width: 140px; /* Aumentado de 60px a 140px */
  height: auto;
  object-fit: contain;
  display: block;
  margin: 0 auto 10px auto; /* Centrado */
} 

.nav-btn {
  display: block;
  background-color: #1a2f20;
  color: #fff;
  text-decoration: none;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 8px;
  text-align: center;
  font-weight: bold;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.nav-btn:hover, .router-link-active {
  background-color: #1a2f20;
  border: 1px solid #CCA300; /* Borde Amarillo */
  color: #CCA300;
}

/* --- ESTILOS DEL CONTENIDO --- */
.content-area {
  flex-grow: 1;
  padding: 40px;
  overflow-y: auto;
}
</style>