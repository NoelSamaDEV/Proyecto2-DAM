<script setup>
import { ref } from 'vue'
import QrcodeVue from 'qrcode.vue'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'

// 1. Recibimos el texto del QR (ej: "Mesa-1") y el título
const props = defineProps({
  contenidoQR: {
    type: String,
    required: true
  },
  titulo: {
    type: String,
    default: 'Código QR'
  }
})

const emit = defineEmits(['close'])

// 2. Referencia para saber qué parte de la pantalla vamos a "fotografiar"
const zonaImpresion = ref(null)

// 3. Función para generar el PDF
const descargarPDF = async () => {
  if (!zonaImpresion.value) return

  // A. Usamos html2canvas para tomar una "foto" del div del QR
  const canvas = await html2canvas(zonaImpresion.value, {
    scale: 3, // Mayor escala = mejor calidad de imagen
    useCORS: true,
    backgroundColor: '#ffffff' // Fondo blanco obligatorio para el PDF
  })

  // B. Convertimos esa foto a formato imagen
  const imgData = canvas.toDataURL('image/png')

  // C. Creamos el PDF (A4 vertical)
  const pdf = new jsPDF('p', 'mm', 'a4')
  
  // D. Calculamos medidas para centrar la imagen
  const pdfWidth = pdf.internal.pageSize.getWidth() // Ancho del A4 (210mm)
  const imgProps = pdf.getImageProperties(imgData)
  
  // Ajustamos el alto manteniendo la proporción original
  const imgHeight = (imgProps.height * pdfWidth) / imgProps.width 
  
  // E. Añadimos la imagen al PDF (posición X: 0, Y: 20)
  pdf.addImage(imgData, 'PNG', 0, 20, pdfWidth, imgHeight)
  
  // F. Descargamos
  pdf.save(`QR-${props.titulo}.pdf`)
}
</script>

<template>
  <div class="modal-fondo" @click.self="$emit('close')">
    <div class="modal-caja">
      
      <button class="btn-cerrar" @click="$emit('close')">✕</button>

      <h3>Vista Previa</h3>

      <div class="area-qr" ref="zonaImpresion">
        <h2 class="titulo-impresion">{{ titulo }}</h2>
        
        <qrcode-vue 
          :value="contenidoQR" 
          :size="250" 
          level="H" 
          render-as="canvas"
        />
        
        <p class="pie-pagina">Escanea para acceder al menú</p>
      </div>
      <div class="acciones">
        <button class="btn-descargar" @click="descargarPDF">
          🖨️ Descargar PDF
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped>
/* Estilo del fondo oscuro */
.modal-fondo {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.7); /* Oscuro transparente */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

/* Estilo de la ventana blanca */
.modal-caja {
  background: white;
  padding: 25px;
  border-radius: 15px;
  width: 400px;
  text-align: center;
  position: relative;
  box-shadow: 0 10px 25px rgba(0,0,0,0.5);
}

.btn-cerrar {
  position: absolute;
  top: 10px; right: 15px;
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #555;
}

/* Estilos de la zona que se va a imprimir */
.area-qr {
  border: 2px dashed #ccc;
  padding: 20px;
  margin: 20px 0;
  border-radius: 10px;
  background-color: white; /* Importante para que el PDF no salga gris */
}

.titulo-impresion {
  margin-bottom: 20px;
  color: #333;
  font-family: sans-serif;
}

.pie-pagina {
  margin-top: 15px;
  color: #666;
  font-size: 14px;
}

/* Botón verde */
.btn-descargar {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  width: 100%;
  transition: background 0.3s;
}

.btn-descargar:hover {
  background-color: #218838;
}
</style>