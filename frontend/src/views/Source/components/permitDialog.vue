<template>
  <div id="mains" class="mains-contain">
    <el-dialog v-model="showPermitDialogs" title="Tips" width="30%" :before-close="handleClose">
      <p>数据粒度</p>
      <p>存储组</p>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel()">Cancel</el-button>
          <el-button type="primary" @click="handleSubmit()">Confirm</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, watch } from 'vue';
import { ElDialog } from 'element-plus';
// import { useI18n } from 'vue-i18n';
// import axios from '@/util/axios.js';
// import { useStore } from 'vuex';
// import { useRouter, useRoute } from 'vue-router';
export default {
  name: 'PermitDialog',
  props: {
    showPermitDialog: {
      type: Boolean,
      default: () => {},
    },
  },
  setup(props, { emit }) {
    // const { t } = useI18n();
    // const router = useRouter();
    // const route = useRoute();
    let showPermitDialogs = ref(false);
    watch(
      () => props.showPermitDialog,
      (val) => {
        showPermitDialogs.value = val;
      }
    );
    const handleClose = () => {
      showPermitDialogs.value = false;
      emit('cancelDialog');
    };
    const handleCancel = () => {
      showPermitDialogs.value = false;
      emit('cancelDialog');
    };
    const handleSubmit = () => {};
    onMounted(() => {
      showPermitDialogs.value = props.showPermitDialog;
    });
    // onActivated(() => {});
    return {
      showPermitDialogs,
      handleClose,
      handleSubmit,
      handleCancel,
    };
  },
  components: {
    ElDialog,
  },
};
</script>

<style scoped lang="scss">
.mains-contain {
  width: 100%;
  height: 100%;
  overflow: auto;
}
</style>
