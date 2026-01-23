import { createRouter, createWebHashHistory } from 'vue-router'
import MesasView from '../views/MesasView.vue'
import DetalleMesaView from '../views/DetalleMesaView.vue'
import NotificacionesView from '../views/NotificacionesView.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', name: 'home', component: MesasView },
    { path: '/mesa/:id', name: 'detalle-mesa', component: DetalleMesaView },
    
    // 2. AÑADIR ESTA LÍNEA:
    { path: '/notificaciones', name: 'notificaciones', component: NotificacionesView }
  ]
})

export default router