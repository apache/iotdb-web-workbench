import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import directive from './directive';
import store from './store';
import { i18n } from 'element-plus/lib/locale';
import 'element-plus/packages/theme-chalk/src/base.scss';
import i18nFile from '@/i18n/index.js';
import '@/styles/reset.scss';
import '@/styles/element.scss';

i18n(i18nFile.global.t);
const app = createApp(App);
directive(app);
app.use(store).use(router).use(i18nFile).mount('#app');
