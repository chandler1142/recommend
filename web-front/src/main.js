// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import {send, getInfo} from '../static/analytics.js'
import ElementUI, {Button, Form, FormItem, Input, Message} from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css';
import axios from 'axios'

Vue.config.productionTip = false

Vue.use(ElementUI)
Vue.use(Button)
Vue.use(Form)
Vue.use(FormItem)
Vue.use(Input)
Vue.prototype.$message = Message

Vue.directive("click-stat", {
  bind(el, binding) {
    el.addEventListener("click", (e) => {
      console.log(e)
      let clickData = binding.value;
      let info = getInfo(clickData, e);
      console.log(info)
      send(info);
    })
  }
})

axios.defaults.baseURL = 'http://localhost:8081'//请求的地址
axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
Vue.prototype.$http = axios

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: {App},
  template: '<App/>'
})
