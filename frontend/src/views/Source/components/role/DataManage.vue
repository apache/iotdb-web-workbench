<!-- 角色数据管理权限 -->
<template>
  <div class="data-manage">
    <el-button type="primary" @click="addPermit">添加权限</el-button>
    <permit-dialog ref="permitDialogRef"></permit-dialog>
  </div>
</template>

<script>
import { ref } from 'vue';
import PermitDialog from '../permitDialog';

export default {
  name: 'DataManage',
  props: [],
  setup() {
    let permitDialogRef = ref(null);
    let addPermit = () => {
      permitDialogRef.value.open({ type: 'add', origin: 'role' });
    };
    return {
      permitDialogRef,
      addPermit,
    };
  },
  components: {
    PermitDialog,
  },
};
</script>
<style scoped lang="scss"></style>
