import { watch, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
function useLanguageWatch(refValue, callback) {
  let { locale } = useI18n();
  watch(locale, () => {
    refValue.value = [];
    nextTick(() => {
      refValue.value = callback();
    });
  });
}

export default useLanguageWatch;
