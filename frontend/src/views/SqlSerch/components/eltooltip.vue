<template>
  <el-tooltip class="item" effect="light" :content="label" placement="top">
    <slot></slot>
  </el-tooltip>
</template>

<script>
import { ElTooltip } from 'element-plus';
export default {
  props: {
    label: String,
  },
  components: {
    ElTooltip,
  },
};
</script>

<style></style>
