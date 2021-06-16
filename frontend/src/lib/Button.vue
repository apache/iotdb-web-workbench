<template>
  <el-button v-bind="$attrs"><slot></slot></el-button>
</template>

<script>
import { ElButton } from "element-plus";
export default {
  name: "IotButton",
  inheritAttrs: false,
  setup() {},
  props: [],
  components: {
    ElButton,
  },
};
</script>

<style scoped lang="scss"></style>
