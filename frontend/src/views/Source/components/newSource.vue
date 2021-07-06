<template>
  <div class="new-source">
    <el-dialog
      :title="types == 1 ? $t('sourcePage.editDialogTitle') : $t('sourcePage.addDialogTitle')"
      :lock-scroll="true"
      :model-value="showDialog"
      width="520px"
      :close-on-click-modal="false"
      @close="$emit('close')"
    >
      <!-- label-width="100px" -->

      <el-form ref="formRef" :model="form" :rules="rules" class="source-form">
        <el-form-item :label="$t('sourcePage.alias')" prop="alias">
          <el-input v-model="form.alias"></el-input>
        </el-form-item>
        <el-form-item :label="$t('sourcePage.host')" prop="host">
          <el-input v-model="form.host"></el-input>
          <span class="eg">{{ $t('sourcePage.eg') }}</span>
        </el-form-item>
        <el-form-item :label="$t('sourcePage.port')" prop="port">
          <el-input v-model="form.port"></el-input>
        </el-form-item>
        <el-form-item :label="$t('sourcePage.username')" prop="username">
          <el-input v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item :label="$t('sourcePage.password')" prop="password">
          <el-input v-model="form.password" show-password></el-input>
        </el-form-item>
        <el-form-item :label="$t('sourcePage.test')">
          <el-button @click="testConnect()">{{ $t('sourcePage.testBtnLabel') }}</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="$emit('close')">{{ $t('common.cancel') }}</el-button>
          <el-button type="primary" @click="submit()">{{ $t('common.submit') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, reactive, ref } from 'vue';
import { ElDialog, ElButton, ElForm, ElInput, ElFormItem, ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import axios from '@/util/axios.js';
import { useStore } from 'vuex';
// import { useRoute } from 'vue-router';

export default {
  name: 'NewSource',
  props: {
    showDialog: {
      type: Boolean,
      required: true,
    },
    types: {
      type: Number,
    },
    serverId: {
      type: Number,
    },
    data: {
      type: Object,
    },
    func: {
      type: Object,
    },
  },
  setup(props, context) {
    const { t } = useI18n();
    const store = useStore();
    // const router = useRoute();
    let form = reactive({
      alias: '',
      host: '',
      port: '',
      username: '',
      password: '',
    });
    const formRef = ref(null);
    const rules = reactive({
      alias: [
        {
          required: true,
          message: t(`sourcePage.aliasEmptyTip`),
          trigger: 'change',
        },
      ],
      password: [
        {
          required: true,
          message: t(`sourcePage.passwordEmptyTip`),
          trigger: 'change',
        },
      ],
      host: [
        {
          required: true,
          message: t(`sourcePage.hostEmptyTip`),
          trigger: 'change',
        },
        {
          pattern: /^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$/,
          message: t(`sourcePage.hostErrorTip`),
          trigger: 'change',
        },
      ],
      port: [
        {
          required: true,
          message: t(`sourcePage.portEmptyTip`),
          trigger: 'change',
        },
        {
          pattern: /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/,
          message: t(`sourcePage.portErrorTip`),
          trigger: 'change',
        },
      ],
      username: [
        {
          required: true,
          message: t(`sourcePage.usernameEmptyTip`),
          trigger: 'change',
        },
      ],
    });
    // const isHost = (rule, value, callback) => {
    //   let reg = /^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$/;
    //   console.log(rule);
    //   if (!reg.test(value)) {
    //     return callback(new Error('请输入0-100的整数'));
    //   } else {
    //     callback();
    //   }
    // };
    const submit = () => {
      formRef.value.validate((valid) => {
        let connection = {
          alias: form.alias,
          host: form.host,
          port: form.port,
          username: form.username,
          password: form.password,
          id: form.id || null,
          userId: store.state.userInfo.userId || null,
        };
        if (valid) {
          axios.post('/servers', { ...connection }).then((res) => {
            if (res && res.code == 0) {
              ElMessage.success('新增或数据连接成功');
              context.emit('successFunc', res);
              props.func.updateTree();
            }
          });
        }
      });
    };
    const getBaseInfo = () => {
      axios.get(`/servers/${props.serverId}`, {}).then((res) => {
        if (res && res.code == 0) {
          form.alias = res.data.alias;
          form.host = res.data.host;
          form.port = res.data.port;
          form.username = res.data.username;
          form.password = res.data.password;
          form.id = res.data.id || null;
        }
      });
    };
    const testConnect = () => {
      let patternReg = /^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$/;
      if (!form.host) {
        ElMessage.error(t(`sourcePage.hostEmptyTip`));
        return false;
      }
      if (!patternReg.test(form.host)) {
        ElMessage.error(t(`sourcePage.hostErrorTip`));
        return false;
      }

      axios
        .get('/test', {
          params: {
            host: form.host,
          },
        })
        .then((rs) => {
          if (rs && rs.code == 0) {
            ElMessage.success(t(`sourcePage.testResult`));
          }
        });
    };
    onMounted(() => {
      if (props.types == 1) {
        getBaseInfo();
      }
    });

    return {
      form,
      formRef,
      rules,
      submit,
      getBaseInfo,
      testConnect,
    };
  },
  components: {
    ElDialog,
    ElButton,
    ElForm,
    ElInput,
    ElFormItem,
  },
};
</script>

<style scoped lang="scss">
.new-source {
  .source-form {
    .eg {
      font-size: 12px;

      color: rgba(34, 34, 34, 0.4);
    }
  }
  &::v-deep .el-form-item__content {
    line-height: 20px;
  }
  &::v-deep .el-dialog {
    margin-top: 10vh !important;
  }
}
</style>
