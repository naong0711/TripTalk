import { createApp } from 'vue'
import App from './App.vue'
import router from '@/router/index.js'
import CKEditor from '@ckeditor/ckeditor5-vue'
// import 'bootstrap/dist/css/bootstrap.css'
// import 'bootstrap/dist/js/bootstrap.bundle.min.js'

const app = createApp(App)
app.use(router)

app.use(CKEditor)
app.mount('#app')
