<!-- 授权用户弹窗 -->
<template>
  <el-dialog title="授权用户" width="520px" :append-to-body="true" v-model="visible">
    <div class="dialog-content">
      <!-- <div class="left">
          
      </div>
      <div class="right"></div> -->
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="visible = false">{{ $t('common.submit') }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { ref } from 'vue';
export default {
  name: 'DialogGrantUser',
  props: [],
  setup() {
    let visible = ref(false);

    const open = ({ type, data } = {}) => {
      visible.value = true;
      console.log(type, data);
    };
    return { visible, open };
  }
};
</script>
<style scoped lang="scss">
.dialog-content {
  .left {
  }
  .right {
  }
}
</style>
