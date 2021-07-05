const storage = {
  state: () => {},
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
