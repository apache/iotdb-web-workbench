import { createStore } from 'vuex';
import moduleA from './moduleA';
// import storage from './storage';

export default createStore({
  state: () => ({
    isLogin: false,
  }),
  mutations: {
    setLogin(state, islogin) {
      state.isLogin = islogin;
    },
  },
  actions: {
    async fetchIsLogin({ commit }) {
      commit('setLogin', true);
    },
  },
  modules: {
    a: moduleA,
    // storage,
  },
});
