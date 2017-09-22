// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuetify from 'vuetify'
import App from './App'
import router from './router'

// import 'vuetify/dist/vuetify.min.css'

Vue.use(Vuetify)

class EasyHKStock {

  constructor () {
    this.user = null
    this.menus = null
    this.vue = null
    this.init()
  }
  init () {
    // Vue.http.options.root = config.urlRoot
    Promise.all([
      this.initUser(),
      this.initMenus()
    ]).then(data => {
      this.initVue()
    })
  }

  initUser () {
// return Vue.http.get(config.userUrl)
// .then(response=>{
// this.user = response.data
// sessionStorage.user = this.user
// return true
// })
    this.user = 'test'
    sessionStorage.user = this.user
  }

  initMenus () {
// return Vue.http.get(config.menusUrl)
// .then(response=>{
// this.menus = response.data;
// return true;
// })
    this.menus = [{title: 'test', href: '/test'}, {title: 'shareholding', href: '/shareholding'}]
  }

  initVue () {
    Vue.config.productionTip = false
    this.vue = new Vue({
      el: '#app',
      router,
      render: h => h(App, {props: {menus: this.menus}})
    })
  }

  test () {
    console.log(this.menus)
  }
}

let app = new EasyHKStock()
app.test()
// Vue.config.productionTip = false
//
// /* eslint-disable no-new */
// new Vue({
// el: '#app',
// router,
// template: '<App/>',
// components: { App }
// })
