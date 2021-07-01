import axios from '@/util/axios.js';

const storage = {
  state: () => {
    groupDetailInfo: {
    }
  },
  mutations: {
    setGroupDetail(state, data) {
      state.groupDetailInfo = data;
    },
  },
  actions: {
    async fetchGroupDetail({ commit }) {
      commit('setGroupDetail', true);
    },
  },
  getters: {},
};
export default storage;
