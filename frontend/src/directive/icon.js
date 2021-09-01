export default (app) => {
  app.directive('icon', {
    mounted(el, binding) {
      let ele = el.getElementsByTagName('use')[0];
      let val = ele.getAttribute('xlink:href');
      el.onmouseleave = () => {
        ele.setAttribute('xlink:href', val);
      };
      el.onmouseenter = () => {
        ele.setAttribute('xlink:href', binding.value);
      };
    },
  });
};
