import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import router from './router'

// 使用ElementUI
Vue.use(ElementUI)

Vue.config.productionTip = false

// 创建Vue实例
new Vue({
  router,
  render: (h) => h(App)
}).$mount('#app')
