import { ref } from "vue";
import Vrouter from "@/router";
function useLangSwitch() {
  const langMap = {
    cn: 0,
    en: 1,
  };
  const lang = langMap[localStorage.getItem("lang") || "cn"];
  const langIndex = ref(lang);

  const handleLangCommand = (val) => {
    const langIndexMap = {
      0: "cn",
      1: "en",
    };
    localStorage.setItem("lang", langIndexMap[val]);
    window.onbeforeunload = null;
    const router = Vrouter;
    router.replace({ name: "DataBaseM" }).then(() => {
      location.reload();
    });
  };

  return {
    langIndex,
    handleLangCommand,
  };
}

export default useLangSwitch;
