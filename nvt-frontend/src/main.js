Vue.config.devtools = false
console.clear()


import Vue from 'vue'
import App from './components/app/app'
//import 'bootstrap/dist/css/bootstrap.css'
import './main.css'
import 'font-awesome/css/font-awesome.css'
import router from './routes'
import 'vuetify/dist/vuetify.min.css'

import Vuetify from 'vuetify'

Vue.use(Vuetify)

new Vue({
    el: '#app',
    render: h => h(App),
    router
})
