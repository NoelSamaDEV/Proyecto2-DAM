<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const mesas = ref([])

// 1. Cargar mesas existentes (GET)
const cargarMesas = async () => {
  try {
    const respuesta = await fetch('http://localhost:8080/api/mesas')
    if (respuesta.ok) {
      mesas.value = await respuesta.json()
    } else {
      console.error("Error al cargar mesas")
    }
  } catch (error) {
    console.error("Error de conexión:", error)
  }
}

// 2. Crear una mesa nueva automática (POST)
const crearNuevaMesa = async () => {
  try {
    const respuesta = await fetch('http://localhost:8080/api/mesas', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    if (respuesta.ok) {
      await cargarMesas() // Recargamos la lista
    } else {
      alert("Error al crear la mesa")
    }
  } catch (error) {
    console.error("Error conectando con el servidor:", error)
    alert("Error de conexión")
  }
}

// 3. Ir al detalle de la mesa (Usando el ID real)
const irAMesa = (id) => {
  router.push(`/mesa/${id}`)
}

// Cargar al inicio
onMounted(() => {
  cargarMesas()
})
</script>

<template>
  <div class="mesas-page">
    <div class="header-mesas">
        <h1>MESAS</h1>
    </div>

    <div class="grid-mesas">
      
      <div v-for="mesa in mesas" :key="mesa.idMesa" class="card-mesa">
        <div class="estado-icon">
          <span v-if="mesa.estado === 'LIBRE'" title="Libre">🟢</span>
          <span v-else-if="mesa.estado === 'OCUPADA'" title="Ocupada">🔴</span>
          <span v-else title="Pidiendo Cuenta">⚠️</span>
        </div>

        <h2>MESA {{ mesa.numeroMesa }}</h2>
        
        <button class="btn-ver" @click="irAMesa(mesa.idMesa)">
            VER MESA
        </button>
      </div>

      <div class="card-mesa nueva-mesa" @click="crearNuevaMesa">
        <div class="plus-icon">+</div>
        <span>NUEVA MESA</span>
      </div>

    </div>
  </div>
</template>

<style scoped>
.mesas-page { width: 100%; }

.header-mesas {
    border-bottom: 3px solid #CCA300;
    margin-bottom: 20px;
    padding-bottom: 5px;
    width: 100%;
}

h1 { color: #000; font-weight: bold; margin: 0; }

.grid-mesas {
    display: grid;
    /* Ajuste automático para que queden bonitas */
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
}

.card-mesa {
    background-color: #737373;
    border-radius: 10px;
    height: 180px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: white;
    position: relative;
    box-shadow: 0 4px 6px rgba(0,0,0,0.3);
    transition: transform 0.2s;
}

.estado-icon {
    position: absolute;
    top: 10px;
    right: 10px;
    font-size: 1.2rem;
}

.btn-ver {
    background-color: #223E2A;
    color: #CCA300;
    border: 2px solid #CCA300;
    padding: 8px 20px;
    border-radius: 5px;
    font-weight: bold;
    cursor: pointer;
    margin-top: 15px;
}

.btn-ver:hover {
    background-color: #1a2f20;
}

.nueva-mesa {
    background-color: #e0e0e0;
    color: #555;
    border: 3px dashed #CCA300;
    cursor: pointer;
}

.nueva-mesa:hover {
    background-color: #d0d0d0;
    transform: scale(1.02);
}

.plus-icon {
    font-size: 3rem;
    color: #CCA300;
    font-weight: bold;
}
</style>