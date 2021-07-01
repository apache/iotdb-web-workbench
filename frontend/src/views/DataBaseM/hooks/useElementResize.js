import { ref } from 'vue';

function useLangSwitch(domRef, dividerWidth) {
  let startX = ref(0);
  let endX = ref(0);
  let initWidth = dividerWidth.value;
  domRef.value.onmousedown = (e) => {
    document.documentElement.style.cursor = 'w-resize';
    startX.value = e.clientX;
    endX.value = e.clientX;
    document.onmousemove = function (e) {
      endX.value = e.clientX;
      dividerWidth.value = initWidth + (endX.value - startX.value);
    };
    document.onmouseup = function () {
      document.documentElement.style.cursor = 'auto';
      initWidth = dividerWidth.value;
      document.onmousemove = null;
      document.onmouseup = null;
    };
  };
}

export default useLangSwitch;
