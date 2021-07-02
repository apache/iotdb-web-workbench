import { ref } from 'vue';

function useLangSwitch(domRef, divwerHeight) {
  let startY = ref(0);
  let endY = ref(0);
  let initWidth = divwerHeight.value;
  domRef.value.onmousedown = (e) => {
    document.documentElement.style.cursor = 'n-resize';
    startY.value = e.clientY;
    endY.value = e.clientY;
    document.onmousemove = function (e) {
      if (divwerHeight.value > 30) {
        endY.value = e.clientY;
        divwerHeight.value = initWidth + (startY.value - endY.value);
      } else {
        divwerHeight.value = 0;
      }
      console.log(divwerHeight.value);
    };
    document.onmouseup = function () {
      document.documentElement.style.cursor = 'auto';
      initWidth = divwerHeight.value;
      document.onmousemove = null;
      document.onmouseup = null;
    };
  };
}

export default useLangSwitch;
