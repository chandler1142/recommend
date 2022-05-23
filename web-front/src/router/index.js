import Vue from 'vue'
import Router from 'vue-router'
import List from '@/components/List'
import Info from '@/components/Info'
import Login from '@/components/Login'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path:'/',
      redirect:'/login'
    },
    {
      path:'/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/list',
      name: 'List',
      component: List
    },
    {
      path: '/info/:id',
      name: 'Info',
      component: Info
    }
  ]
})

router.beforeEach((to, from, next) => {
  if(to.path === '/login') return next()
  const tokenStr = window.sessionStorage.getItem("token")
  if(!tokenStr) return next('/login')
  next()
})

export default router
