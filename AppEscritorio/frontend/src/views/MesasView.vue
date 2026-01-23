<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import ModalQR from '../components/ModalQR.vue'

const router = useRouter()
const mesas = ref([])
const nuevaMesaNumero = ref('') 
let intervalo = null

// VARIABLES MODAL QR
const mostrarModal = ref(false)
const datosQR = ref('')
const nombreMesaQR = ref('')

// 1. CARGAR MESAS (Con aviso de error en consola)
const cargarMesas = async () => {
  console.log("🔄 Intentando cargar mesas...")
  try {
    const res = await fetch('http://localhost:8080/api/mesas')
    if (res.ok) {
      mesas.value = await res.json()
      console.log("✅ Mesas cargadas:", mesas.value.length)
    } else {
      console.error("❌ Error del servidor al cargar mesas")
    }
  } catch (error) { 
    console.error("❌ Error de conexión (Backend apagado?):", error) 
  }
}

// 2. BOTÓN DE REFRESCAR MANUAL
const forzarRecarga = () => {
    // alert("Refrescando datos...") // Descomenta si quieres ver un aviso visual
    cargarMesas()
}

// 3. CREAR MESA
const crearMesa = async () => {
  console.log("➕ Botón crear pulsado. Valor input:", nuevaMesaNumero.value)
  
  if (!nuevaMesaNumero.value || nuevaMesaNumero.value.trim() === '') {
    alert("⚠️ Por favor, escribe un número en el recuadro 'Nº'")
    return
  }

  try {
    const res = await fetch('http://localhost:8080/api/mesas', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ numeroMesa: nuevaMesaNumero.value, estado: 'LIBRE' })
    })
    
    if (res.ok) {
      nuevaMesaNumero.value = '' // Limpiar input
      cargarMesas()
    } else {
      alert("❌ Error: Esa mesa ya existe.")
    }
  } catch (e) { 
    console.error(e)
    alert("Error de conexión al crear")
  }
}

// 4. BORRAR MESA
const borrarMesa = async (id, numero) => {
  if (!confirm(`¿Eliminar la Mesa ${numero}?`)) return

  try {
    const res = await fetch(`http://localhost:8080/api/mesas/${id}`, { method: 'DELETE' })
    if (res.ok) cargarMesas()
    else alert("No se puede borrar (tiene pedidos activos).")
  } catch (e) { console.error(e) }
}

// 5. VER QR
const verQR = (mesa) => {
  datosQR.value = `https://foodnow.app/mesa/${mesa.idMesa}`
  nombreMesaQR.value = `Mesa ${mesa.numeroMesa}`
  mostrarModal.value = true
}

const irAMesa = (id) => {
  router.push(`/mesa/${id}`)
}

onMounted(() => {
  cargarMesas()
  // intervalo = setInterval(cargarMesas, 5000) // Aumentamos tiempo para no saturar si hay error
})

onUnmounted(() => {
  if(intervalo) clearInterval(intervalo)
})
</script>

<template>
  <div class="home-container">
    
    <div class="top-panel">
      <h1>Panel de Sala</h1>
      
      <div class="acciones-top">
        <input 
            type="text" 
            v-model="nuevaMesaNumero" 
            placeholder="Nº" 
            class="input-numero" 
        />
        
        <button class="btn-crear" @click="crearMesa">
           <span>+</span> Crear
        </button>

        <button class="btn-refresh" @click="forzarRecarga" title="Actualizar lista">
            🔄
        </button>
      </div>
    </div>

    <div class="grid-mesas">
      <div v-for="mesa in mesas" :key="mesa.idMesa" class="card-mesa">
        
        <div class="card-header">
           <div class="icon-circle">🍽️</div>
           <div class="info-mesa">
             <h3>MESA {{ mesa.numeroMesa }}</h3>
             <span v-if="mesa.estado === 'LIBRE'" class="badge libre">LIBRE</span>
             <span v-else-if="mesa.estado === 'OCUPADA'" class="badge ocupada">OCUPADA</span>
             <span v-else-if="mesa.estado === 'PIDIENDO_CUENTA'" class="badge cuenta">CUENTA</span>
             <span v-else class="badge ayuda">AYUDA</span>
           </div>
        </div>

        <button class="btn-ver-mesa" @click="irAMesa(mesa.idMesa)">VER MESA</button>

        <div class="card-footer">
           <button class="btn-small" @click="verQR(mesa)">📱 QR</button>
           <button class="btn-small btn-trash" @click="borrarMesa(mesa.idMesa, mesa.numeroMesa)">🗑️</button>
        </div>

      </div>
    </div>

    <div v-if="mesas.length === 0" class="vacio">
        <p>No hay mesas o no hay conexión con el servidor.</p>
        <small>Revisa que el Backend (Java) esté ejecutándose.</small>
    </div>

    <ModalQR v-if="mostrarModal" :contenidoQR="datosQR" :titulo="nombreMesaQR" @close="mostrarModal = false" />
  </div>
</template>

<style scoped>
.home-container { padding: 20px; font-family: 'Segoe UI', sans-serif; }

/* PANEL SUPERIOR */
.top-panel {
  background: white; padding: 15px 30px; border-radius: 12px;
  display: flex; justify-content: space-between; align-items: center;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05); margin-bottom: 30px;
  position: relative; 
  z-index: 10; /* IMPORTANTE: Asegura que esté por encima */
}

h1 { margin: 0; font-size: 1.8rem; font-weight: bold; color: #000; }

.acciones-top { display: flex; gap: 10px; align-items: center; }

/* INPUT REFORZADO */
.input-numero {
  width: 60px;
  padding: 10px;
  border: 2px solid #ddd; /* Borde más visible */
  border-radius: 6px;
  text-align: center;
  font-size: 1.1rem;
  background: white;
  color: black;
  cursor: text;
}
.input-numero:focus { border-color: #223E2A; outline: none; }

.btn-crear {
  background-color: #223E2A; color: white; border: none; padding: 10px 20px;
  border-radius: 6px; font-weight: bold; cursor: pointer;
  display: flex; align-items: center; gap: 5px;
}
.btn-crear:active { transform: scale(0.95); } /* Efecto visual al clicar */

.btn-refresh {
  background: white; border: 1px solid #ddd; padding: 10px; border-radius: 6px;
  cursor: pointer; font-size: 1.2rem;
}
.btn-refresh:active { background-color: #eee; }

/* GRID */
.grid-mesas { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 20px; z-index: 1; }

/* TARJETA */
.card-mesa {
  background: white; border-radius: 15px; padding: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05); display: flex; flex-direction: column;
  gap: 15px; transition: transform 0.2s;
}
.card-mesa:hover { transform: translateY(-3px); }

.card-header { display: flex; align-items: center; gap: 15px; }
.icon-circle { width: 50px; height: 50px; background-color: #E0E0E0; border-radius: 50%; display: flex; justify-content: center; align-items: center; font-size: 1.5rem; }
.info-mesa h3 { margin: 0 0 5px 0; font-size: 1.1rem; color: #333; font-weight: 800; }

/* BADGES */
.badge { font-size: 0.75rem; padding: 3px 8px; border-radius: 4px; font-weight: bold; }
.libre { background-color: #E8F5E9; color: #2E7D32; }
.ocupada { background-color: #FFEBEE; color: #C62828; }
.cuenta { background-color: #E3F2FD; color: #1565c0; }
.ayuda { background-color: #FFF3E0; color: #EF6C00; }

.btn-ver-mesa { background-color: #223E2A; color: white; border: none; padding: 12px; border-radius: 6px; font-weight: bold; cursor: pointer; width: 100%; }
.btn-ver-mesa:hover { background-color: #1a2f20; }

.card-footer { display: flex; gap: 10px; }
.btn-small { flex: 1; background: white; border: 1px solid #eee; padding: 8px; border-radius: 6px; cursor: pointer; display: flex; justify-content: center; align-items: center; gap: 5px; }
.btn-small:hover { background-color: #f9f9f9; }
.btn-trash:hover { background-color: #FFEBEE; color: red; border-color: red; }

.vacio { text-align: center; margin-top: 50px; color: #777; }
</style>