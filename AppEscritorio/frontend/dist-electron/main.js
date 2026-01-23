import { app, BrowserWindow } from "electron";
import path from "node:path";
import { fileURLToPath } from "node:url";
const __filename$1 = fileURLToPath(import.meta.url);
path.dirname(__filename$1);
let win;
const createWindow = () => {
  win = new BrowserWindow({
    width: 1200,
    height: 800,
    title: "FoodNow - Gestión",
    // Nota: Si el icono no carga, no te preocupes por ahora, lo importante es que arranque.
    icon: path.join(process.env.PUBLIC || "", "favicon.ico"),
    webPreferences: {
      // IMPORTANTE: Si no tienes un archivo 'preload.js' en la carpeta electron,
      // comenta o borra la siguiente línea, o te dará otro error.
      // preload: path.join(__dirname, 'preload.js'), 
      nodeIntegration: true,
      contextIsolation: false
    }
  });
  if (process.env.VITE_DEV_SERVER_URL) {
    win.loadURL(process.env.VITE_DEV_SERVER_URL);
  } else {
    win.loadFile("dist/index.html");
  }
};
app.whenReady().then(createWindow);
app.on("window-all-closed", () => {
  if (process.platform !== "darwin") app.quit();
});
