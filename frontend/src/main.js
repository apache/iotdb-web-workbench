import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import directive from "./directive";
import store from "./store";
import ElementLocale from "element-plus/lib/locale";
import "element-plus/packages/theme-chalk/src/base.scss";
import i18n from "@/i18n/index";
import "@/styles/reset.scss";
import "@/styles/element.scss";

ElementLocale.i18n(i18n.global.t);
const app = createApp(App);
directive(app);
app.use(store).use(router).use(i18n).mount("#app");
