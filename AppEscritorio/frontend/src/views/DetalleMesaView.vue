<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ModalQR from '../components/ModalQR.vue'
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'
// Importamos tu logo (asegúrate de que el archivo existe en esta ruta)
import logoUrl from '@/assets/logo.png' 

const route = useRoute()
const router = useRouter()
const idMesa = route.params.id

// --- ESTADO ---
const mesa = ref(null)
const pedidoActual = ref(null)
const cargando = ref(true)
const mostrarModal = ref(false)
const datosQR = ref('')
const nombreMesaQR = ref('')
let intervalo = null

// --- MEMORIA PARA LOS TICKS (Checkboxes) ---
const memoriaEntregados = ref(new Set())

const toggleLinea = (idLinea) => {
  if (memoriaEntregados.value.has(idLinea)) {
    memoriaEntregados.value.delete(idLinea)
  } else {
    memoriaEntregados.value.add(idLinea)
  }
}

// --- CARGA DE DATOS ---
const cargarDatos = async () => {
  // Solo mostramos 'Cargando...' la primera vez para no parpadear en el refresco
  if (!mesa.value) cargando.value = true
  
  try {
    const resMesa = await fetch(`http://localhost:8080/api/mesas/${idMesa}`)
    if (resMesa.ok) mesa.value = await resMesa.json()
    
    const resPedido = await fetch(`http://localhost:8080/api/pedidos/mesa/${idMesa}/actual`)
    if (resPedido.ok) pedidoActual.value = await resPedido.json()
    else pedidoActual.value = null

  } catch (e) { console.error(e) } 
  finally { cargando.value = false }
}

// --- LÓGICA NOTIFICACIONES (Atender Ayuda/Cuenta) ---
const realizarAccion = async (e) => {
    setTimeout(async () => {
        await fetch(`http://localhost:8080/api/mesas/${idMesa}/atender`, { method: 'POST' })
        cargarDatos()
        e.target.checked = false // Reset visual del checkbox
    }, 500)
}

// --- ACCIONES DE MESA ---
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

// --- UTILIDAD: PROCESAR IMAGEN PARA PDF ---
const cargarImagen = (src) => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => {
      const canvas = document.createElement('canvas')
      canvas.width = img.width
      canvas.height = img.height
      const ctx = canvas.getContext('2d')
      ctx.drawImage(img, 0, 0)
      resolve(canvas.toDataURL('image/png'))
    }
    img.onerror = reject
    img.src = src
  })
}

// --- GENERAR PDF DEL TICKET ---
const imprimirTicket = async () => {
  if (!pedidoActual.value) return alert("No hay pedido para imprimir")

  // Formato tipo ticket (80mm ancho, altura variable según contenido)
  const doc = new jsPDF({
    orientation: 'portrait',
    unit: 'mm',
    format: [80, 240] 
  })

  try {
    // 1. LOGO (Arriba del todo y centrado)
    const imgData = await cargarImagen(logoUrl)
    // Ajusta las coordenadas (x, y, ancho, alto) según tu logo
    doc.addImage(imgData, 'PNG', 25, 2, 30, 30)
  } catch (e) {
    console.warn("No se pudo cargar el logo:", e)
  }

  // 2. DATOS RESTAURANTE
  doc.setFontSize(12)
  doc.setFont("helvetica", "bold")
  doc.text("FOODNOW RESTAURANT", 40, 38, { align: "center" }) // Bajamos Y para dejar sitio al logo
  
  doc.setFontSize(9)
  doc.setFont("helvetica", "normal")
  doc.text("C/ Ejemplo, 123", 40, 43, { align: "center" })
  doc.text("Madrid, España", 40, 47, { align: "center" })

  doc.setLineWidth(0.5)
  doc.line(5, 52, 75, 52) // Línea separadora

  // 3. METADATOS DEL TICKET
  const fecha = new Date().toLocaleString()
  doc.setFontSize(8)
  doc.text(`Fecha: ${fecha}`, 5, 58)
  doc.text(`Mesa: ${mesa.value.numeroMesa}`, 5, 62)
  doc.text(`Ref Pedido: #${pedidoActual.value.idPedido}`, 5, 66)

  // 4. TABLA DE PRODUCTOS
  const columnas = ["Cant", "Producto", "Total"]
  const filas = pedidoActual.value.lineasPedido.map(l => [
    `${l.cantidad}`,
    l.producto?.nombre || 'Producto',
    `${l.subtotal.toFixed(2)}`
  ])

  autoTable(doc, {
    startY: 70,
    head: [columnas],
    body: filas,
    theme: 'plain', // Estilo minimalista
    styles: { fontSize: 8, cellPadding: 1 },
    headStyles: { fontStyle: 'bold', borderBottomWidth: 0.5 },
    columnStyles: {
      0: { cellWidth: 10, halign: 'center' },
      2: { cellWidth: 15, halign: 'right' }
    },
    margin: { left: 5, right: 5 }
  })

  // 5. TOTAL
  const finalY = doc.lastAutoTable.finalY + 6
  doc.setFontSize(12)
  doc.setFont("helvetica", "bold")
  doc.text(`TOTAL: ${pedidoActual.value.total.toFixed(2)} €`, 75, finalY, { align: "right" })

  // 6. PIE DE PÁGINA
  doc.setFontSize(9)
  doc.setFont("helvetica", "italic")
  doc.text("¡Gracias por su visita!", 40, finalY + 12, { align: "center" })
  doc.text("www.foodnow.app", 40, finalY + 16, { align: "center" })

  // Guardar PDF
  doc.save(`Ticket_Mesa_${mesa.value.numeroMesa}.pdf`)
}

// Formateador de texto
const formatoTexto = (estado) => {
    if (estado === 'PIDIENDO_CUENTA') return 'Pidiendo Cuenta'
    if (estado === 'AYUDA') return 'Ayuda'
    return estado
}

onMounted(() => {
  cargarDatos()
  intervalo = setInterval(cargarDatos, 2000)
})

onUnmounted(() => clearInterval(intervalo))
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
              
              <div v-for="linea in pedidoActual.lineasPedido" :key="linea.idLinea" 
                   class="item" :class="{ 'entregado': memoriaEntregados.has(linea.idLinea) }">
                
                <div class="info-izquierda">
                   <label class="checkbox-wrapper">
                      <input type="checkbox" 
                             :checked="memoriaEntregados.has(linea.idLinea)" 
                             @change="toggleLinea(linea.idLinea)">
                      <span class="custom-box-verde"></span>
                   </label>

                   <span><b>{{ linea.cantidad }}x</b> {{ linea.producto?.nombre }}</span>
                </div>

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
         <div class="grupo-botones-izq">
             <button class="btn-qr" @click="abrirQR">📱 VER QR</button>
             
             <button class="btn-imprimir" @click="imprimirTicket">
                 🖨️ IMPRIMIR CUENTA
             </button>
         </div>
         
         <button class="btn-vaciar" @click="vaciarMesa">LIBERAR MESA</button>
      </footer>
    </div>
    
    <ModalQR v-if="mostrarModal" :contenidoQR="datosQR" :titulo="nombreMesaQR" @close="mostrarModal = false" />
  </div>
</template>

<style scoped>
.detalle-page { padding: 20px; background: #E0E0E0; min-height: 100vh; font-family: sans-serif; }
.header-detalle { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }

/* BADGES ESTADO */
.badge { padding: 5px 15px; border-radius: 5px; color: white; font-weight: bold; text-transform: uppercase; text-align: center; }
.LIBRE { background: green; } .OCUPADA { background: #c62828; } .AYUDA, .naranja { background: #ef6c00; } .PIDIENDO_CUENTA, .azul { background: #1565c0; }

/* PANELES */
.paneles-container { display: flex; gap: 20px; }
.panel { background: white; padding: 20px; border-radius: 10px; border: 2px solid #223E2A; display: flex; flex-direction: column; }
.pedido-panel { flex: 1.5; }
.columna-derecha { flex: 1; display: flex; flex-direction: column; }
.notif-panel { height: 100%; }

/* ITEM PEDIDO */
.item { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px dashed #ccc; transition: all 0.2s; }
.info-izquierda { display: flex; align-items: center; gap: 15px; }

/* EFECTO TACHADO AL MARCAR */
.item.entregado { opacity: 0.5; }
.item.entregado .info-izquierda span { text-decoration: line-through; }

/* --- CHECKBOX VERDE CORPORATIVO (#223E2A) --- */
.checkbox-wrapper { position: relative; width: 24px; height: 24px; cursor: pointer; }
.checkbox-wrapper input { position: absolute; opacity: 0; cursor: pointer; }

/* El cuadrado */
.custom-box-verde {
  position: absolute; top: 0; left: 0; height: 24px; width: 24px;
  background-color: white; 
  border: 2px solid #223E2A; /* Borde Verde */
  border-radius: 4px;
  transition: all 0.2s;
}

/* Relleno al marcar */
.checkbox-wrapper input:checked ~ .custom-box-verde { background-color: #223E2A; }

/* El Tick Blanco */
.custom-box-verde:after {
  content: ""; position: absolute; display: none; left: 7px; top: 3px; width: 6px; height: 12px;
  border: solid white; border-width: 0 3px 3px 0; transform: rotate(45deg);
}
.checkbox-wrapper input:checked ~ .custom-box-verde:after { display: block; }
/* --------------------------------------------- */

/* CHECKBOX GRANDE (NOTIFICACIONES) */
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

.total { font-size: 1.5rem; font-weight: bold; margin-top: 20px; text-align: right; }

/* FOOTER */
.footer { margin-top: 20px; display: flex; justify-content: space-between; align-items: center; }
.grupo-botones-izq { display: flex; gap: 10px; }

.btn-vaciar { background: #333; color: white; padding: 15px 30px; border: none; border-radius: 5px; cursor: pointer; font-size: 1rem; font-weight: bold; }
.btn-qr, .btn-atras { padding: 10px 20px; cursor: pointer; border: 1px solid #ccc; background: white; border-radius: 5px; font-weight: bold; }

/* BOTÓN IMPRIMIR (ESTILO NUEVO) */
.btn-imprimir {
  background-color: #223E2A; 
  color: white; 
  padding: 10px 20px; 
  border: none; 
  border-radius: 5px; 
  cursor: pointer; 
  font-weight: bold; 
  font-size: 1rem;
  transition: background 0.2s;
}
.btn-imprimir:hover { background-color: #1a2f20; }
</style>