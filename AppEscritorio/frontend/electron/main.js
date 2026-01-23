import { app, BrowserWindow } from 'electron'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

// --- ARREGLO PARA __dirname ---
// Como usas "type": "module" en package.json, __dirname no existe por defecto.
// Lo creamos manualmente así:
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
// ------------------------------

// Desactiva la aceleración de hardware si da problemas (opcional)
// app.disableHardwareAcceleration()

let win

const createWindow = () => {
  win = new BrowserWindow({
    width: 1200,
    height: 800,
    title: "FoodNow - Gestión",
    // Nota: Si el icono no carga, no te preocupes por ahora, lo importante es que arranque.
    icon: path.join(process.env.PUBLIC || '', 'favicon.ico'), 
    webPreferences: {
      // IMPORTANTE: Si no tienes un archivo 'preload.js' en la carpeta electron,
      // comenta o borra la siguiente línea, o te dará otro error.
      // preload: path.join(__dirname, 'preload.js'), 
      
      nodeIntegration: true,
      contextIsolation: false,
    },
  })

  // EN DESARROLLO: Carga la URL de Vite
  if (process.env.VITE_DEV_SERVER_URL) {
    win.loadURL(process.env.VITE_DEV_SERVER_URL)
  } else {
    // EN PRODUCCIÓN: Carga el archivo html compilado
    win.loadFile('dist/index.html')
  }
}

app.whenReady().then(createWindow)

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit()
})