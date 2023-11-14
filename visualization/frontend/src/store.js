import Vue from 'vue';
import Vuex from 'vuex'
import ApplicationRestResource from './components/rest/ApplicationRestResource'

Vue.use(Vuex);
export const store = new Vuex.Store({
    state: {
        isDemo: false
    },
    mutations: {
        enableDemo(state) {
        state.isDemo = true;
        }
    }
});

const applicationRestResource = new ApplicationRestResource();
applicationRestResource.getIsDemo().then(isDemo => {
    if (isDemo) {
        store.commit("enableDemo");
    }
});
