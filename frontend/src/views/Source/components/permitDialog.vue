<template>
  <div id="mains" class="mains-contain">
    <el-dialog v-model="showPermitDialogs" :title="type == 1 ? '编辑权限' : '新增权限'" width="30%" :before-close="handleClose">
      <el-form ref="permitFormRef" :model="permitForm" :rules="permitRules" label-position="top" class="permit-form">
        <el-form-item :label="$t('sourcePage.path')"> aaa </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel()">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="handleSubmit()">{{ $t('common.submit') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, watch } from 'vue';
import { ElDialog } from 'element-plus';
import { useI18n } from 'vue-i18n';
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
    type: {
      type: Number,
      default: () => {},
    },
  },
  setup(props, { emit }) {
    const { t } = useI18n();
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
      t,
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
