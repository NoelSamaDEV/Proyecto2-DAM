<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const listaNotificaciones = ref([])
const cargando = ref(true)
let intervalo = null

const cargarNotificaciones = async () => {
  try {
    const res = await fetch('http://localhost:8080/api/mesas')
    if (res.ok) {
      const todas = await res.json()
      listaNotificaciones.value = todas.filter(m => m.estado === 'AYUDA' || m.estado === 'PIDIENDO_CUENTA')
    }
  } catch (e) { console.error(e) } 
  finally { cargando.value = false }
}

const verMesa = (id) => { router.push(`/mesa/${id}`) }

const atender = async (id) => {
  await fetch(`http://localhost:8080/api/mesas/${id}/atender`, { method: 'POST' })
  cargarNotificaciones()
}

onMounted(() => {
  cargarNotificaciones()
  intervalo = setInterval(cargarNotificaciones, 2000)
})

onUnmounted(() => clearInterval(intervalo))
</script>

<template>
  <div class="page-container">
    <h1 class="page-title">Notificaciones</h1>
    
    <div v-if="listaNotificaciones.length === 0" class="empty-state">
        ✅ No hay avisos pendientes
    </div>

    <div v-for="mesa in listaNotificaciones" :key="mesa.idMesa" class="notif-card" 
         :class="mesa.estado === 'PIDIENDO_CUENTA' ? 'azul' : 'naranja'">
      
      <div class="card-content">
          <h3>MESA {{ mesa.numeroMesa }}</h3>
          <p v-if="mesa.estado === 'PIDIENDO_CUENTA'" class="tipo-aviso">💶 Solicita la CUENTA</p>
          <p v-else class="tipo-aviso">🙋 Solicita CAMARERO</p>
      </div>

      <div class="card-actions">
          <button class="btn-ver" @click="verMesa(mesa.idMesa)">Ver Mesa</button>
          <label class="checkbox-container">
              <input type="checkbox" @change="atender(mesa.idMesa)">
              <span class="checkmark"></span>
          </label>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container { padding: 20px; background: #f5f5f5; min-height: 100vh; font-family: sans-serif; }
.page-title { margin-bottom: 20px; color: #333; }
.empty-state { text-align: center; color: #888; font-size: 1.2rem; margin-top: 50px; }

/* DISEÑO COMPACTO */
.notif-card { 
    background: white; 
    padding: 10px 15px; /* Menos padding vertical */
    border-radius: 8px; 
    margin-bottom: 8px; /* Menos separación entre tarjetas */
    display: flex; 
    justify-content: space-between; 
    align-items: center; 
    border-left: 5px solid #ccc; 
    box-shadow: 0 1px 3px rgba(0,0,0,0.1); 
}
.azul { border-left-color: #1565c0; background: #e3f2fd; }
.naranja { border-left-color: #ef6c00; background: #fff3e0; }

.card-content h3 { margin: 0; font-size: 1rem; color: #333; }
.tipo-aviso { margin: 2px 0 0 0; font-weight: bold; color: #555; font-size: 0.9rem; }

.card-actions { display: flex; align-items: center; gap: 15px; }
.btn-ver { 
    padding: 5px 10px; 
    cursor: pointer; 
    background: white; 
    border: 1px solid #333; 
    border-radius: 4px; 
    font-weight: bold; 
    font-size: 0.8rem; 
}

/* CHECKBOX CUADRADO */
.checkbox-container { display: block; position: relative; width: 30px; height: 30px; cursor: pointer; }
.checkbox-container input { opacity: 0; width: 0; height: 0; }
.checkmark { position: absolute; top: 0; left: 0; height: 30px; width: 30px; background-color: #fff; border: 2px solid #555; border-radius: 5px; }
.checkbox-container input:checked ~ .checkmark { background-color: #223E2A; border-color: #223E2A; }
.checkmark:after { content: ""; position: absolute; display: none; left: 10px; top: 4px; width: 6px; height: 14px; border: solid white; border-width: 0 3px 3px 0; transform: rotate(45deg); }
.checkbox-container input:checked ~ .checkmark:after { display: block; }
</style>