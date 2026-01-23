<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// --- ESTADO ---
const listaAvisosMesa = ref([])   
const listaPedidos = ref([])      
const cargando = ref(true)
let intervalo = null

// --- MEMORIA SIMPLE (ID Pedido -> Número de líneas) ---
const memoriaCantidadLineas = ref(new Map())

// --- SONIDO ---
const reproducirSonido = () => {
  console.log("🔔 ¡DING! Novedades en cocina")
  // const audio = new Audio('/assets/notification.mp3'); audio.play().catch(() => {});
}

// --- LÓGICA DE CARGA (POLLING) ---
const cargarTodo = async () => {
  try {
    // 1. CARGAR AVISOS SALA (Ayuda/Cuenta)
    const resMesas = await fetch('http://localhost:8080/api/mesas')
    if (resMesas.ok) {
      const todas = await resMesas.json()
      listaAvisosMesa.value = todas.filter(m => m.estado === 'AYUDA' || m.estado === 'PIDIENDO_CUENTA')
    }

    // 2. CARGAR PEDIDOS COCINA
    const resPedidos = await fetch('http://localhost:8080/api/pedidos/pendientes')
    if (resPedidos.ok) {
      const pedidosNuevos = await resPedidos.json()
      let hayNovedades = false

      // Procesar cada pedido para ver si cambió
      const listaProcesada = pedidosNuevos.map(p => {
        // Contamos cuántas líneas tiene este pedido ahora
        const numLineasActual = p.lineasPedido.length
        
        // Recuperamos cuántas tenía la última vez
        const numLineasAnterior = memoriaCantidadLineas.value.get(p.idPedido)

        // BANDERAS DE ESTADO
        p.etiqueta = "" 
        p.claseBorde = "borde-verde" // Por defecto (sin cambios)

        if (numLineasAnterior === undefined) {
          // CASO 1: NO EXISTÍA -> ES NUEVO
          p.etiqueta = "NUEVO PEDIDO"
          p.claseBorde = "borde-rojo animacion-parpadeo"
          hayNovedades = true
        } 
        else if (numLineasActual > numLineasAnterior) {
          // CASO 2: TIENE MÁS LÍNEAS QUE ANTES -> SE HA AÑADIDO ALGO
          p.etiqueta = "NUEVO PRODUCTO AÑADIDO"
          p.claseBorde = "borde-rojo animacion-parpadeo"
          hayNovedades = true
        }
        else {
          // CASO 3: ESTÁ IGUAL (Mantenemos estado visual anterior si existía)
          const visualAnterior = listaPedidos.value.find(viejo => viejo.idPedido === p.idPedido)
          if (visualAnterior && visualAnterior.etiqueta) {
            p.etiqueta = visualAnterior.etiqueta
            p.claseBorde = visualAnterior.claseBorde
          }
        }

        // Actualizamos la memoria
        memoriaCantidadLineas.value.set(p.idPedido, numLineasActual)
        
        return p
      })

      listaPedidos.value = listaProcesada
      if (hayNovedades) reproducirSonido()
    }

  } catch (e) { console.error(e) } finally { cargando.value = false }
}

// --- ACCIONES ---
const verMesa = (id) => { router.push(`/mesa/${id}`) }

const atenderMesa = async (id) => {
  await fetch(`http://localhost:8080/api/mesas/${id}/atender`, { method: 'POST' })
  cargarTodo()
}

const atenderPedido = async (pedido) => {
  // 1. Cerrar en Backend
  if (pedido.mesa?.idMesa) {
    await fetch(`http://localhost:8080/api/pedidos/mesa/${pedido.mesa.idMesa}/cerrar`, { method: 'POST' })
  }
  // 2. Borrar de Memoria Local (Importante)
  memoriaCantidadLineas.value.delete(pedido.idPedido)
  // 3. Quitar visualmente
  listaPedidos.value = listaPedidos.value.filter(p => p.idPedido !== pedido.idPedido)
}

onMounted(() => {
  cargarTodo()
  intervalo = setInterval(cargarTodo, 2000)
})

onUnmounted(() => clearInterval(intervalo))
</script>

<template>
  <div class="page-container">
    <h1 class="page-title">Centro de Notificaciones</h1>
    
    <div v-if="listaAvisosMesa.length === 0 && listaPedidos.length === 0" class="empty-state">
        ✅ Todo al día
    </div>

    <div v-if="listaPedidos.length > 0" class="seccion-titulo">🍔 Comandas de Cocina</div>
    
    <div v-for="pedido in listaPedidos" :key="pedido.idPedido" 
         class="notif-card" :class="pedido.claseBorde">
      
      <div class="card-content">
          <div class="header-card">
             <h3>MESA {{ pedido.mesa ? pedido.mesa.numeroMesa : '?' }}</h3>
             
             <span v-if="pedido.etiqueta" class="badge-alerta">
               🚨 {{ pedido.etiqueta }}
             </span>
          </div>

          <ul class="lista-items">
            <li v-for="linea in [...pedido.lineasPedido].reverse()" :key="linea.idLinea">
               <strong>{{ linea.cantidad }}x</strong> 
               {{ linea.producto ? linea.producto.nombre : 'Producto' }}
            </li>
          </ul>
          
          <div class="footer-card">
            <span class="precio">Total: {{ pedido.total }} €</span>
            <small class="id-pedido">#{{ pedido.idPedido }}</small>
          </div>
      </div>

      <div class="card-actions">
          <label class="checkbox-container">
              <input type="checkbox" @change="atenderPedido(pedido)">
              <span class="checkmark"></span>
          </label>
      </div>
    </div>


    <div v-if="listaAvisosMesa.length > 0" class="seccion-titulo">🔔 Avisos de Sala</div>
    
    <div v-for="mesa in listaAvisosMesa" :key="mesa.idMesa" class="notif-card" 
         :class="mesa.estado === 'PIDIENDO_CUENTA' ? 'borde-azul' : 'borde-naranja'">
      
      <div class="card-content">
          <h3>MESA {{ mesa.numeroMesa }}</h3>
          <p v-if="mesa.estado === 'PIDIENDO_CUENTA'" class="tipo-aviso">💶 Cuenta</p>
          <p v-else class="tipo-aviso">🙋 Camarero</p>
      </div>

      <div class="card-actions">
          <button class="btn-ver" @click="verMesa(mesa.idMesa)">Ver</button>
          <label class="checkbox-container">
              <input type="checkbox" @change="atenderMesa(mesa.idMesa)">
              <span class="checkmark"></span>
          </label>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container { padding: 20px; background: #f5f5f5; min-height: 100vh; font-family: sans-serif; }
.page-title { margin-bottom: 20px; color: #333; text-align: center; font-weight: 800; text-transform: uppercase; }
.empty-state { text-align: center; color: #888; font-size: 1.2rem; margin-top: 50px; }
.seccion-titulo { font-size: 0.9rem; text-transform: uppercase; color: #777; margin: 25px 0 10px 0; font-weight: bold; border-bottom: 2px solid #ddd; }

/* TARJETA BASE */
.notif-card { 
    background: white; padding: 15px; border-radius: 8px; margin-bottom: 10px; 
    display: flex; justify-content: space-between; align-items: center; 
    border-left: 6px solid #ccc; box-shadow: 0 2px 4px rgba(0,0,0,0.1); 
    transition: all 0.3s;
}

/* COLORES DE BORDE */
.borde-azul { border-left-color: #2196F3; background: #E3F2FD; }   
.borde-naranja { border-left-color: #FF9800; background: #FFF3E0; } 
.borde-verde { border-left-color: #4CAF50; background: #E8F5E9; }
.borde-rojo { border-left-color: #D32F2F; background: #FFEBEE; border-left-width: 8px; }

/* ANIMACIÓN */
.animacion-parpadeo { animation: latido 1s infinite; }
@keyframes latido {
  0% { transform: scale(1); }
  50% { transform: scale(1.01); box-shadow: 0 0 10px rgba(211, 47, 47, 0.3); }
  100% { transform: scale(1); }
}

/* CONTENIDO */
.header-card { display: flex; align-items: center; gap: 10px; margin-bottom: 5px; flex-wrap: wrap; }
.header-card h3 { margin: 0; font-size: 1.1rem; color: #333; }
.badge-alerta { 
  background: #D32F2F; color: white; font-size: 0.75rem; padding: 4px 8px; 
  border-radius: 4px; font-weight: bold; text-transform: uppercase; 
}

.lista-items { margin: 10px 0; padding: 0; list-style: none; color: #444; font-size: 0.95rem; }
.lista-items li { border-bottom: 1px dashed #ddd; padding: 4px 0; }

.footer-card { display: flex; gap: 15px; align-items: center; margin-top: 5px; }
.precio { font-weight: bold; color: #2E7D32; font-size: 1.1rem; }
.id-pedido { color: #999; }
.tipo-aviso { margin: 0; font-weight: bold; color: #555; }

/* BOTONES */
.card-actions { display: flex; align-items: center; gap: 10px; }
.btn-ver { padding: 5px 10px; cursor: pointer; background: white; border: 1px solid #333; border-radius: 4px; font-weight: bold; }

/* CHECKBOX */
.checkbox-container { display: block; position: relative; width: 30px; height: 30px; cursor: pointer; }
.checkbox-container input { opacity: 0; width: 0; height: 0; }
.checkmark { position: absolute; top: 0; left: 0; height: 30px; width: 30px; background-color: #fff; border: 2px solid #aaa; border-radius: 5px; }
.checkbox-container input:checked ~ .checkmark { background-color: #4CAF50; border-color: #4CAF50; }
.checkmark:after { content: ""; position: absolute; display: none; left: 10px; top: 5px; width: 6px; height: 14px; border: solid white; border-width: 0 3px 3px 0; transform: rotate(45deg); }
.checkbox-container input:checked ~ .checkmark:after { display: block; }
</style>