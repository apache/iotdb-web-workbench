import { createStore } from "vuex";
import moduleA from "./moduleA";

export default createStore({
  state: {},
  mutations: {},
  actions: {},
  modules: {
    a: moduleA,
  },
});
