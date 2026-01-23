<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const mesas = ref([])
let intervalo = null

// Cargar mesas
const cargarMesas = async () => {
  try {
    const res = await fetch('http://localhost:8080/api/mesas')
    if (res.ok) mesas.value = await res.json()
  } catch (error) {
    console.error(error)
  }
}

// Función para poner bonito el texto
const formatoTexto = (estado) => {
    if (estado === 'PIDIENDO_CUENTA') return 'Pidiendo Cuenta'
    if (estado === 'AYUDA') return 'Ayuda'
    if (estado === 'OCUPADA') return 'Ocupada'
    return 'Libre'
}

const crearMesa = async () => {
    const numero = prompt("Número de mesa:")
    if(numero) {
        await fetch('http://localhost:8080/api/mesas', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ numeroMesa: numero })
        })
        cargarMesas()
    }
}

const irAMesa = (id) => {
  router.push(`/mesa/${id}`)
}

onMounted(() => {
    cargarMesas()
    intervalo = setInterval(cargarMesas, 2000)
})

onUnmounted(() => clearInterval(intervalo))
</script>

<template>
  <div class="home-container">
    <header class="main-header">
        <h1>Panel de Sala</h1>
        <div class="controls">
            <input type="text" placeholder="Nº" class="input-mesa">
            <button class="btn-crear" @click="crearMesa">➕ Crear Mesa</button>
            <button class="btn-refresh" @click="cargarMesas">🔄</button>
        </div>
    </header>

    <div class="grid-mesas">
        <div v-for="mesa in mesas" :key="mesa.idMesa" class="card-mesa">
            <div class="card-header">
                <div class="icono-mesa">🍽️</div>
                <div class="info-mesa">
                    <h2>MESA {{ mesa.numeroMesa }}</h2>
                    <span class="badge" :class="mesa.estado">
                        {{ formatoTexto(mesa.estado) }}
                    </span>
                </div>
            </div>
            
            <button class="btn-ver" @click="irAMesa(mesa.idMesa)">VER MESA</button>
            
            <div class="card-footer">
                <button class="btn-icon">📱 QR</button>
                <button class="btn-icon">🗑️</button>
            </div>
        </div>
    </div>
  </div>
</template>

<style scoped>
.home-container { padding: 40px; background-color: #f4f4f4; min-height: 100vh; font-family: sans-serif; }
.main-header { background: white; padding: 20px 40px; border-radius: 15px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-bottom: 40px; }
.controls { display: flex; gap: 10px; }
.input-mesa { padding: 10px; border: 1px solid #ddd; border-radius: 5px; width: 60px; text-align: center; }
.btn-crear { background: #223E2A; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; font-weight: bold; }
.btn-refresh { background: white; border: 1px solid #ddd; padding: 10px; border-radius: 5px; cursor: pointer; }

/* REJILLA DE 3 COLUMNAS FIJAS */
.grid-mesas { 
    display: grid; 
    grid-template-columns: repeat(3, 1fr); 
    gap: 30px; 
}

.card-mesa { background: white; border-radius: 20px; padding: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 20px; transition: transform 0.2s; }
.card-mesa:hover { transform: translateY(-5px); }

.card-header { display: flex; gap: 20px; align-items: center; }
.icono-mesa { background: #E0E0E0; width: 60px; height: 60px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 30px; color: #999; }
.info-mesa h2 { margin: 0; font-size: 1.2rem; color: #333; }

/* BADGES CON TEXTO CENTRADO */
.badge { 
    display: inline-block; 
    padding: 4px 10px; 
    border-radius: 4px; 
    font-size: 0.8rem; 
    font-weight: bold; 
    margin-top: 5px; 
    text-transform: uppercase; 
    text-align: center; /* <--- IMPORTANTE */
}
.LIBRE { background: #e8f5e9; color: #2e7d32; }
.OCUPADA { background: #ffebee; color: #c62828; }
.AYUDA { background: #fff3e0; color: #ef6c00; } 
.PIDIENDO_CUENTA { background: #e3f2fd; color: #1565c0; }

.btn-ver { background: #223E2A; color: white; border: none; padding: 12px; border-radius: 8px; font-weight: bold; cursor: pointer; width: 100%; }
.card-footer { display: flex; gap: 10px; }
.btn-icon { flex: 1; background: white; border: 1px solid #eee; padding: 8px; border-radius: 8px; cursor: pointer; color: #666; }
</style>