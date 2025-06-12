import Vue from 'vue'
import App from './App.vue'
import router from './router/router'
import axios from 'axios';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.use(ElementUI);

Vue.prototype.$axios = axios.create({

});

Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')

axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response) {
            if (error.response.status === 401) {
                router.push('/login').catch(() => {});
            } else {
                console.error('请求错误:', error.response.status, error.response.data);
            }
        } else {
            console.error('网络错误:', error.message);
        }
        return Promise.reject(error);
    },
);

axios.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
}, error => {
    return Promise.reject(error)
})


