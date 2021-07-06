<template>
  <el-tooltip class="item" effect="light" :content="$t(label)" placement="top">
    <slot></slot>
  </el-tooltip>
</template>

<script>
import { ElTooltip } from 'element-plus';
export default {
  props: {
    label: String,
  },
  setup() {},
  components: {
    ElTooltip,
  },
};
</script>

<style></style>
