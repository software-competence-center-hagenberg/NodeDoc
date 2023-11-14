import Vue from 'vue'
import App from './App.vue'
import { store } from './store'
import vuetify from './plugins/vuetify'
import { library } from '@fortawesome/fontawesome-svg-core'
import {
  faFileExport,
  faTrash,
  faFileDownload,
  faCogs,
  faExchangeAlt,
  faPlusCircle
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

library.add(faFileExport, faTrash, faFileDownload, faCogs, faExchangeAlt, faPlusCircle);

Vue.component('font-awesome-icon', FontAwesomeIcon);
Vue.config.productionTip = false;

new Vue({
  vuetify,
  render: h => h(App),
  store
}).$mount('#app')