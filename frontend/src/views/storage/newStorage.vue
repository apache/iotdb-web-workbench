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
  <div class="new-storage-container">
    <p>{{ $t('storagePage.alias') }}:{{ alias }}</p>
    <el-form ref="formRef" :model="form" :rules="rules" class="source-form" label-position="top">
      <el-form-item :label="$t('storagePage.groupName')" prop="groupName" class="form-input-item require-style">
        <el-input :disabled="router.currentRoute.value.params.groupname" v-model="form.groupName" :placeholder="$t('storagePage.groupNamePlaceholder')">
          <template #prepend>root.</template>
        </el-input>
      </el-form-item>
      <el-form-item :label="$t('storagePage.groupDescription')" prop="description" class="form-input-item">
        <el-input v-model="form.description"></el-input>
      </el-form-item>
      <el-form-item :label="$t('storagePage.ttl')" class="form-input-item" prop="ttl">
        <el-input v-model="form.ttl" min="0" max="9007199254740992" class="ttl-input"></el-input>
        <el-select v-model="form.ttiUnit" class="ttl-input unit" clearable placeholder="  ">
          <el-option :label="$t('storagePage.millsSecondLabel')" value="millisecond"> </el-option>
          <el-option :label="$t('storagePage.secondLabel')" value="second"> </el-option>
          <el-option :label="$t('storagePage.minuteLabel')" value="minute"> </el-option>
          <el-option :label="$t('storagePage.hourLabel')" value="hour"> </el-option>
          <el-option :label="$t('storagePage.dayLabel')" value="day"> </el-option>
          <el-option :label="$t('storagePage.weekLabel')" value="week"> </el-option>
          <el-option :label="$t('storagePage.monthLabel')" value="month"> </el-option>
          <el-option :label="$t('storagePage.yearLabel')" value="year"> </el-option>
        </el-select>
        <i class="tips">{{ $t('storagePage.tips') }}</i>
      </el-form-item>
    </el-form>
    <div class="submit-btns">
      <el-button @click="cancel()">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" @click="submit()">{{ $t('common.submit') }}</el-button>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, reactive, onActivated } from 'vue';
import { ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { useRouter, useRoute } from 'vue-router';
import axios from '@/util/axios.js';

export default {
  name: 'NewStorages',
  props: ['func', 'data'],
  setup(props) {
    const { t } = useI18n();
    const router = useRouter();
    const route = useRoute();
    let formRef = ref(null);
    let alias = ref(null);
    const rules = reactive({
      groupName: [
        {
          required: true,
          message: () => {
            return t(`storagePage.groupNamePlaceholder`);
          },
          trigger: 'blur',
        },
        {
          pattern: /^(["'a-zA-Z0-9_\u4e00-\u9fa5]*)$/,
          message: () => {
            return t(`sourcePage.newUserErrorTip`);
          },
          trigger: 'blur',
        },
        {
          min: 0,
          max: 255,
          message: () => {
            return t(`storagePage.groupNameLengthTips`);
          },
          trigger: 'blur',
        },
      ],
      description: [
        {
          required: false,
          min: 0,
          max: 100,
          message: () => {
            return t(`storagePage.descriptionLengthTips`);
          },
          trigger: 'blur',
        },
      ],
      ttl: [
        {
          required: false,
          pattern: /^[1-9]\d*$/,
          message: () => {
            return t(`sourcePage.ttlErrorTips`);
          },
          trigger: 'blur',
        },
      ],
    });
    let form = reactive({
      groupName: '',
      description: '',
      ttl: '',
      ttiUnit: '',
    });
    /**
     * cancel new storage
     */
    const cancel = () => {
      props.func.removeTab(props.data.id);
      // props.func.addTab(router.currentRoute.value.params.serverid + 'connection');
    };
    /**
     * get storage detail
     * serverid: connect id
     * groupname:storage name
     */
    const getGroupDetail = () => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}/storageGroups/${router.currentRoute.value.params.groupname}`, {}).then((res) => {
        if (res && res.code == 0) {
          form.groupName = res.data.groupName.split('').splice(5).join('');
          form.ttl = res.data.ttl;
          form.ttiUnit = res.data.ttiUnit;
          form.description = res.data.description;
          alias.value = res.data.alias;
        }
      });
    };
    /**
     * new/edit storage
     */
    const submit = () => {
      formRef.value.validate((valid) => {
        if (valid) {
          if (form.ttl && form.ttl > 9007199254740992) {
            ElMessage.error(t('sourcePage.maxErrorTips'));
            return false;
          }
          if ((form.ttl && !form.ttiUnit) || (!form.ttl && form.ttiUnit)) {
            ElMessage.error(t('storagePage.ttlErrTips'));
            return false;
          }
          const reqObj = {
            groupName: 'root.' + form.groupName,
            description: form.description,
            ttl: form.ttl == '' || form.ttl == null ? null : +form.ttl,
            ttlUnit: form.ttiUnit || null,
          };
          axios.post(`/servers/${router.currentRoute.value.params.serverid}/storageGroups`, { ...reqObj }).then((res) => {
            if (res && res.code == 0) {
              ElMessage.success(t('sourcePage.newGroupSuccessLabel'));
              props.func.removeTab(props.data.id);
              props.func.updateTreeByIds([props.data.id]);
              props.func.expandByIds([props.data.id]);
              props.func.addTab(router.currentRoute.value.params.serverid + 'connectionroot.' + form.groupName + 'storageGroup');
            }
          });
        }
      });
    };
    /**
     * get souce alisa
     */
    const getServerName = () => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}`, {}).then((res) => {
        if (res && res.code == 0) {
          alias.value = res.data.alias;
        }
      });
    };
    onMounted(() => {
      if (router.currentRoute.value.params.groupname) {
        getGroupDetail();
      } else {
        getServerName();
      }
    });
    onActivated(() => {
      if (route.params.forceupdate) {
        form.groupName = null;
        form.ttl = null;
        form.ttiUnit = null;
        form.description = null;
      }
    });

    return {
      // t,
      formRef,
      rules,
      form,
      submit,
      cancel,
      alias,
      router,
    };
  },
  components: {
    ElForm,
    ElFormItem,
    ElInput,
    ElSelect,
    ElOption,
    ElButton,
  },
};
</script>

<style scoped lang="scss">
.new-storage-container {
  text-align: left;
  padding: 20px;
  .el-form {
    margin-top: 20px;
    .form-input-item {
      width: 450px;
    }
    .require-style {
      // margin-left: -11px;
    }
    .ttl-input {
      display: inline-block;
      width: 310px;
    }
    .unit {
      width: 130px;
      margin-left: 10px;
    }
    .tips {
      color: rgba(34, 34, 34, 0.4);
      font-size: 12px;
      position: absolute;
      top: 46px;
      left: 0;
    }
  }

  .submit-btns {
    text-align: center;
    margin-top: 35px;

    .el-button {
      width: 110px;
      // padding-left: 0 !important;
    }
  }
}
</style>
