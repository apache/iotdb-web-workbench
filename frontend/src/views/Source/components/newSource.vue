<!--
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
-->

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
        <el-form-item class="test-form-item" :label="$t('sourcePage.test')">
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
      port: '6667',
      username: '',
      password: '',
    });
    const formRef = ref(null);
    const rules = reactive({
      alias: [
        {
          required: true,
          message: () => {
            return t(`sourcePage.aliasEmptyTip`);
          },
          trigger: 'change',
        },
        {
          pattern: /^\S+$/,
          message: () => {
            return t(`sourcePage.newUserErrorTip`);
          },
          trigger: 'change',
        },
        {
          min: 3,
          max: 100,
          message: () => {
            return t(`sourcePage.aliasErrorTip`);
          },
          trigger: 'change',
        },
      ],
      password: [
        {
          required: true,
          message: () => {
            return t(`sourcePage.passwordEmptyTip`);
          },
          trigger: 'change',
        },
      ],
      host: [
        {
          required: true,
          message: () => {
            return t(`sourcePage.hostEmptyTip`);
          },
          trigger: 'change',
        },
        {
          pattern: /^(((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?))|(localhost)$/,
          message: () => {
            return t(`sourcePage.hostErrorTip`);
          },
          trigger: 'change',
        },
        // {
        //   pattern: /^localhost$/,
        //   message: t(`sourcePage.hostErrorTip`),
        //   trigger: 'change',
        // },
      ],
      port: [
        {
          required: true,
          message: () => {
            return t(`sourcePage.portEmptyTip`);
          },
          trigger: 'change',
        },
        {
          pattern: /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/,
          message: () => {
            return t(`sourcePage.portErrorTip`);
          },
          trigger: 'change',
        },
      ],
      username: [
        {
          required: true,
          message: () => {
            return t(`sourcePage.usernameEmptyTip`);
          },
          trigger: 'change',
        },
      ],
    });
    /**
     * new/edit source
     */
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
              ElMessage.success(t(`sourcePage.newSourceSuccessLabel`));
              context.emit('successFunc', res);
              props.func.updateTree();
            }
          });
        }
      });
    };
    /**
     * get source basic info
     */
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
    /**
     * source can test successful
     */
    const testConnect = () => {
      let patternReg = /^(((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?))|(localhost)$/;
      let portReg = /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/;
      if (!form.host) {
        ElMessage.error(t(`sourcePage.hostEmptyTip`));
        return false;
      }
      if (!patternReg.test(form.host)) {
        ElMessage.error(t(`sourcePage.hostErrorTip`));
        return false;
      }

      if (!form.port) {
        ElMessage.error(t(`sourcePage.portEmptyTip`));
        return false;
      }
      if (!portReg.test(form.port)) {
        ElMessage.error(t(`sourcePage.portErrorTip`));
        return false;
      }

      if (!form.username) {
        ElMessage.error(t(`sourcePage.usernameEmptyTip`));
        return false;
      }
      if (!form.password) {
        ElMessage.error(t(`sourcePage.passwordEmptyTip`));
        return false;
      }
      axios
        .post('/test', {
          host: form.host,
          port: form.port,
          username: form.username,
          password: form.password,
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
  &:deep(.el-form-item__content) {
    line-height: 20px;
  }
  &:deep(.el-dialog) {
    margin-top: 10vh !important;
  }
  &:deep(.el-form .el-form-item .el-form-item__label) {
    width: 100px !important;
  }
  .source-form {
    .eg {
      font-size: 12px;
      color: rgba(34, 34, 34, 0.4);
    }
  }
  .test-form-item {
    margin-top: 20px;
  }
}
</style>
