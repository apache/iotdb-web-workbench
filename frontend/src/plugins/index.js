import element_plus from './element_plus';
import event_bus from './event_bus';

const plugins = [element_plus, event_bus];

export default {
  install(Vue) {
    plugins.forEach((plugin) => {
      Vue.use(plugin);
    });
  },
};
