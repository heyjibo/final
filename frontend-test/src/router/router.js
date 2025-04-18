import Vue from 'vue'
import Router from 'vue-router'
import CustomerTest from '../components/CustomerTest.vue' 
import DeliverTest from '../components/DeliverTest.vue'

Vue.use(Router)

export default new Router({
  routes: [
    { path: '/customer', component: CustomerTest },
    { path: '/deliver', component: DeliverTest },
    { path: '/', redirect: '/customer' }
  ]
})