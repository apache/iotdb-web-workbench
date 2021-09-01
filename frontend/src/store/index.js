import { createStore } from 'vuex';
import moduleA from './moduleA';
import axios from '@/util/axios.js';
// import storage from './storage';

export default createStore({
  state: () => ({
    isLogin: false,
    userInfo: {},
    firstPageLoad: true,
  }),
  mutations: {
    setFirstPageLoad(state, bool) {
      state.firstPageLoad = bool;
    },
    setLogin(state, islogin) {
      state.isLogin = islogin;
    },
    setUserInfo(state, info) {
      state.userInfo = info.data;
    },
  },
  actions: {
    async fetchIsLogin({ commit }) {
      axios.get('/get', {}).then((res) => {
        if (res?.code === '0') {
          commit('setLogin', true);
          commit('setUserInfo', {
            data: {
              name: res?.data?.name,
              userId: res?.data?.id,
            },
          });
        }
      });
    },
  },
  modules: {
    a: moduleA,
    // storage,
  },
});
