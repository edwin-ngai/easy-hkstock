import Vue from 'vue'
import Router from 'vue-router'
import Hello from '@/components/Hello'
import Shareholding from '@/components/Shareholding'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/test',
      name: 'Hello',
      component: Hello
    },
    {
      path: '/shareholding',
      name: 'Shareholding',
      component: Shareholding
    }
  ]
})
