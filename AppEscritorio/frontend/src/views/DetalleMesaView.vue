<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ModalQR from '../components/ModalQR.vue'

const route = useRoute()
const router = useRouter()
const idMesa = route.params.id

const mesa = ref(null)
const pedidoActual = ref(null)
const cargando = ref(true)
const mostrarModal = ref(false)
const datosQR = ref('')
const nombreMesaQR = ref('')

// CARGAR DATOS
const cargarDatos = async () => {
  cargando.value = true
  try {
    const resMesa = await fetch(`http://localhost:8080/api/mesas/${idMesa}`)
    if (resMesa.ok) mesa.value = await resMesa.json()
    
    const resPedido = await fetch(`http://localhost:8080/api/pedidos/mesa/${idMesa}/actual`)
    if (resPedido.ok) pedidoActual.value = await resPedido.json()
    else pedidoActual.value = null

  } catch (e) { console.error(e) } 
  finally { cargando.value = false }
}

// LOGICA CHECKBOX (ATENDER)
const realizarAccion = async (e) => {
    setTimeout(async () => {
        await fetch(`http://localhost:8080/api/mesas/${idMesa}/atender`, { method: 'POST' })
        cargarDatos()
        e.target.checked = false // Reset visual del checkbox
    }, 500)
}

// ACCIONES
const vaciarMesa = async () => {
  if (!confirm("¿Cobrar y liberar mesa?")) return
  await fetch(`http://localhost:8080/api/mesas/${idMesa}/liberar`, { method: 'POST' })
  cargarDatos()
}

const abrirQR = () => {
  if (!mesa.value) return
  datosQR.value = `https://foodnow.app/mesa/${mesa.value.idMesa}`
  nombreMesaQR.value = `Mesa ${mesa.value.numeroMesa}`
  mostrarModal.value = true
}

// Formateador de texto simple para el header
const formatoTexto = (estado) => {
    if (estado === 'PIDIENDO_CUENTA') return 'Pidiendo Cuenta'
    if (estado === 'AYUDA') return 'Ayuda'
    return estado
}

onMounted(() => cargarDatos())
</script>

<template>
  <div class="detalle-page">
    <div v-if="mesa" class="contenido-mesa">
      
      <header class="header-detalle">
        <button class="btn-atras" @click="router.push('/')">⬅ Volver</button>
        <h1>MESA {{ mesa.numeroMesa }}</h1>
        <div class="header-right">
           <span class="badge" :class="mesa.estado">{{ formatoTexto(mesa.estado) }}</span>
        </div>
      </header>

      <div class="paneles-container">
        
        <div class="panel pedido-panel">
          <h2>PEDIDO ACTUAL</h2>
          <div class="lista-items">
            <div v-if="pedidoActual && pedidoActual.lineasPedido?.length">
              <div v-for="linea in pedidoActual.lineasPedido" :key="linea.idLinea" class="item">
                <span><b>{{ linea.cantidad }}x</b> {{ linea.producto?.nombre }}</span>
                <span>{{ linea.subtotal.toFixed(2) }}€</span>
              </div>
            </div>
            <div v-else><p>Vacío</p></div>
          </div>
          <div class="total">Total: {{ pedidoActual ? pedidoActual.total.toFixed(2) : '0.00' }}€</div>
        </div>

        <div class="columna-derecha">
            <div class="panel notif-panel">
                <h2>NOTIFICACIONES</h2>
                
                <div v-if="mesa.estado === 'PIDIENDO_CUENTA'" class="alerta azul">
                    <div class="texto">
                        <span class="icono">💶</span> 
                        <div><strong>Pide Cuenta</strong><br><small>Marcar al cobrar</small></div>
                    </div>
                    <label class="checkbox-container">
                        <input type="checkbox" @change="realizarAccion">
                        <span class="checkmark"></span>
                    </label>
                </div>

                <div v-else-if="mesa.estado === 'AYUDA'" class="alerta naranja">
                     <div class="texto">
                        <span class="icono">🙋</span> 
                        <div><strong>Ayuda</strong><br><small>Marcar al atender</small></div>
                    </div>
                    <label class="checkbox-container">
                        <input type="checkbox" @change="realizarAccion">
                        <span class="checkmark"></span>
                    </label>
                </div>

                <div v-else class="ok-state">👍 Todo en orden</div>
            </div>
        </div>
      </div>

      <footer class="footer">
         <button class="btn-qr" @click="abrirQR">QR</button>
         <button class="btn-vaciar" @click="vaciarMesa">Liberar Mesa</button>
      </footer>
    </div>
    <ModalQR v-if="mostrarModal" :contenidoQR="datosQR" :titulo="nombreMesaQR" @close="mostrarModal = false" />
  </div>
</template>

<style scoped>
.detalle-page { padding: 20px; background: #E0E0E0; min-height: 100vh; font-family: sans-serif; }
.header-detalle { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }

/* BADGE HEADER CON TEXTO CENTRADO */
.badge { 
    padding: 5px 15px; 
    border-radius: 5px; 
    color: white; 
    font-weight: bold; 
    text-transform: uppercase; 
    text-align: center; /* <--- IMPORTANTE */
}
.LIBRE { background: green; } .OCUPADA { background: #c62828; } .AYUDA, .naranja { background: #ef6c00; } .PIDIENDO_CUENTA, .azul { background: #1565c0; }

.paneles-container { display: flex; gap: 20px; }
.panel { background: white; padding: 20px; border-radius: 10px; border: 2px solid #223E2A; display: flex; flex-direction: column; }
.pedido-panel { flex: 1.5; }
.columna-derecha { flex: 1; display: flex; flex-direction: column; }
.notif-panel { height: 100%; }

/* CHECKBOX CUADRADO */
.checkbox-container { display: block; position: relative; width: 40px; height: 40px; cursor: pointer; }
.checkbox-container input { opacity: 0; width: 0; height: 0; }
.checkmark { position: absolute; top: 0; left: 0; height: 40px; width: 40px; background-color: #fff; border: 2px solid #555; border-radius: 6px; }
.checkbox-container:hover input ~ .checkmark { background-color: #f0f0f0; }
.checkbox-container input:checked ~ .checkmark { background-color: #223E2A; border-color: #223E2A; }
.checkmark:after { content: ""; position: absolute; display: none; left: 13px; top: 6px; width: 10px; height: 20px; border: solid white; border-width: 0 4px 4px 0; transform: rotate(45deg); }
.checkbox-container input:checked ~ .checkmark:after { display: block; }

/* ALERTAS */
.alerta { padding: 15px; border-radius: 8px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; border: 1px solid #ccc; }
.azul { background: #e3f2fd; border-left: 5px solid #1565c0; }
.naranja { background: #fff3e0; border-left: 5px solid #ef6c00; }
.texto { display: flex; gap: 10px; align-items: center; }
.icono { font-size: 2rem; }
.ok-state { text-align: center; color: #888; margin-top: 50px; font-size: 1.1rem; }

.item { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px dashed #ccc; }
.total { font-size: 1.5rem; font-weight: bold; margin-top: 20px; text-align: right; }

.footer { margin-top: 20px; display: flex; justify-content: space-between; }
.btn-vaciar { background: #333; color: white; padding: 15px 30px; border: none; border-radius: 5px; cursor: pointer; font-size: 1rem; }
.btn-qr, .btn-atras { padding: 10px 20px; cursor: pointer; }
</style>