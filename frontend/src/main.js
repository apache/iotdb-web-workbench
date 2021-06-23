import { createApp } from "vue";
import { createI18n } from "vue-i18n";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementLocale from "element-plus/lib/locale";
import enLocale from "element-plus/lib/locale/lang/en";
import zhLocale from "element-plus/lib/locale/lang/zh-cn";
import "element-plus/packages/theme-chalk/src/base.scss";
import messages from "@/i18n/index";
import "@/styles/reset.scss";
import "@/styles/element.scss";

let langMap = {
  cn: zhLocale.name,
  en: enLocale.name,
};
let locale = localStorage.getItem("lang") || "cn";
const i18n = createI18n({
  locale: langMap[locale],
  fallbackLocale: enLocale.name,
  messages,
});
ElementLocale.i18n(i18n.global.t);
createApp(App).use(store).use(router).use(i18n).mount("#app");
