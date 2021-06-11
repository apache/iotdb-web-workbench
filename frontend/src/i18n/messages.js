import enLocale from "element-plus/lib/locale/lang/en";
import zhLocale from "element-plus/lib/locale/lang/zh-cn";

let messages = {
  [enLocale.name]: {
    el: enLocale.el,
    message: {
      hello: "hello world",
    },
  },
  [zhLocale.name]: {
    el: zhLocale.el,
    message: {
      hello: "你好，世界",
    },
  },
};

export default messages;
