<template>
  <div class="home">
    <HelloWorld msg="Welcome to Your Vue.js App" />
    <el-button>默认按钮</el-button>
    <p>{{ $t("message.hello") }}</p>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from "@/components/HelloWorld.vue";
import { ElButton } from "element-plus";

export default {
  name: "Home",
  components: {
    HelloWorld,
    ElButton,
  },
};
</script>

<style scoped lang="scss">
.home {
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
