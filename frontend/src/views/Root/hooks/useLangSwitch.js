import { ref } from 'vue';
import i18n from '@/i18n/index';
import enLocale from 'element-plus/lib/locale/lang/en';
import zhLocale from 'element-plus/lib/locale/lang/zh-cn';
function useLangSwitch() {
  const langMap = { cn: 0, en: 1 };
  const lang = langMap[localStorage.getItem('lang') || 'cn'];
  const langIndex = ref(lang);
  const handleLangCommand = (val) => {
    const langIndexMap = {
      0: 'cn',
      1: 'en',
    };
    localStorage.setItem('lang', langIndexMap[val]);
    langIndex.value = +val;
    i18n.global.locale = [zhLocale.name, enLocale.name][val];
  };
  return {
    langIndex,
    handleLangCommand,
  };
}

export default useLangSwitch;
