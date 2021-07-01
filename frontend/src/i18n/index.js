import en from './en.js';
import cn from './cn.js';
import enLocale from 'element-plus/lib/locale/lang/en';
import zhLocale from 'element-plus/lib/locale/lang/zh-cn';
import { createI18n } from 'vue-i18n';

let messages = {
    ...en,
    ...cn,
};

let langMap = {
    cn: zhLocale.name,
    en: enLocale.name,
};
let locale = localStorage.getItem('lang') || 'cn';
let i18n = createI18n({
    locale: langMap[locale],
    fallbackLocale: enLocale.name,
    messages,
});

export default i18n;
