import { ref } from "vue";

function useLangSwitch(domRef, dividerWidth) {
  let startX = ref(0);
  let endX = ref(0);
  let initWidth = dividerWidth.value;
  domRef.value.onmousedown = (e) => {
    startX.value = e.clientX;
    endX.value = e.clientX;
    document.onmousemove = function (e) {
      endX.value = e.clientX;
      dividerWidth.value = initWidth + (endX.value - startX.value);
    };
    document.onmouseup = function () {
      initWidth = dividerWidth.value;
      document.onmousemove = null;
      document.onmouseup = null;
    };
  };
}

export default useLangSwitch;
