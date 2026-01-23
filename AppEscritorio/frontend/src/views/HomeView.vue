<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const mesas = ref([])
let intervalo = null

// --- CARGAR MESAS ---
const cargarMesas = async () => {
  try {
    const res = await fetch('http://localhost:8080/api/mesas')
    if (res.ok) {
      mesas.value = await res.json()
    }
  } catch (error) {
    console.error(error)
  }
}

// --- CREAR NUEVA MESA ---
const crearMesa = async () => {
  // Pedimos el número al usuario de forma sencilla
  const numero = prompt("Introduce el NÚMERO de la nueva mesa:")
  
  // Si cancela o no escribe nada, paramos
  if (!numero) return

  try {
    const res = await fetch('http://localhost:8080/api/mesas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
          numeroMesa: numero, 
          estado: 'LIBRE' // Nace libre por defecto
      })
    })

    if (res.ok) {
      cargarMesas() // Recargamos para ver la nueva mesa
    } else {
      alert("Error: Quizás esa mesa ya existe.")
    }
  } catch (e) {
    console.error(e)
    alert("Error de conexión al crear mesa")
  }
}

// --- NAVEGACIÓN ---
const irAMesa = (id) => {
  router.push(`/mesa/${id}`)
}

onMounted(() => {
  cargarMesas()
  intervalo = setInterval(cargarMesas, 2000) // Refresco automático
})

onUnmounted(() => clearInterval(intervalo))
</script>

<template>
  <div class="sala-container">
    
    <header class="header-sala">
      <h1>SALA PRINCIPAL</h1>
      
      <button class="btn-crear" @click="crearMesa">
        + Nueva Mesa
      </button>
    </header>

    <div v-if="mesas.length > 0" class="grid-mesas">
      <div 
        v-for="mesa in mesas" 
        :key="mesa.idMesa" 
        class="card-mesa"
        :class="mesa.estado" 
        @click="irAMesa(mesa.idMesa)"
      >
        <div class="numero">{{ mesa.numeroMesa }}</div>
        <div class="estado">{{ mesa.estado }}</div>
        
        <div class="icono-estado">
           <span v-if="mesa.estado === 'LIBRE'">🟢</span>
           <span v-else-if="mesa.estado === 'OCUPADA'">🔴</span>
           <span v-else-if="mesa.estado === 'PIDIENDO_CUENTA'">💶</span>
           <span v-else>🙋</span>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <p>No hay mesas creadas.</p>
      <p>Pulsa en <b>+ Nueva Mesa</b> para empezar.</p>
    </div>

  </div>
</template>

<style scoped>
.sala-container { padding: 20px; }

/* CABECERA */
.header-sala { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  margin-bottom: 30px; 
  border-bottom: 2px solid #ddd;
  padding-bottom: 15px;
}

h1 { color: #333; margin: 0; }

/* BOTÓN CREAR (VERDE) */
.btn-crear {
  background-color: #223E2A;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  transition: transform 0.2s;
}
.btn-crear:hover {
  background-color: #1a2f20;
  transform: scale(1.05);
}

/* GRID */
.grid-mesas {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); /* Adaptable */
  gap: 20px;
}

/* TARJETA DE MESA */
.card-mesa {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
  transition: all 0.3s;
  border: 3px solid transparent;
  position: relative;
  overflow: hidden;
}

.card-mesa:hover { transform: translateY(-5px); box-shadow: 0 8px 15px rgba(0,0,0,0.15); }

/* ESTADOS VISUALES */
.card-mesa.LIBRE { border-color: #4CAF50; color: #2E7D32; }
.card-mesa.OCUPADA { border-color: #F44336; color: #C62828; background-color: #FFEBEE; }
.card-mesa.PIDIENDO_CUENTA { border-color: #2196F3; background-color: #E3F2FD; animation: palpitar 1s infinite; }
.card-mesa.AYUDA { border-color: #FF9800; background-color: #FFF3E0; animation: palpitar 1s infinite; }

.numero { font-size: 2.5rem; font-weight: 900; margin-bottom: 5px; }
.estado { font-size: 0.8rem; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; }
.icono-estado { font-size: 1.5rem; margin-top: 10px; }

.empty-state { text-align: center; color: #777; margin-top: 50px; font-size: 1.2rem; }

@keyframes palpitar {
  0% { transform: scale(1); }
  50% { transform: scale(1.02); }
  100% { transform: scale(1); }
}
</style>