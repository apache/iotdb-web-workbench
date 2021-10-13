import mitt from 'mitt';
const emitter = mitt();

export default (app) => {
  app.config.globalProperties.emitter = emitter;
};
