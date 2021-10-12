import element_plus from './element_plus';

const plugins = [element_plus];

export default {
  install(Vue) {
    plugins.forEach((plugin) => {
      Vue.use(plugin);
    });
  },
};
