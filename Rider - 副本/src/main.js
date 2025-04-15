import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import router from './router'

// Use ElementUI
Vue.use(ElementUI)

Vue.config.productionTip = false

// Create Vue instance
new Vue({
  el: '#app',
  router,
  render: h => h(App)
})
