<template>
  <div class="new-storage-container">
    <p>{{ $t('storagePage.alias') }}:{{ alias }}</p>
    <el-form ref="formRef" :model="form" :rules="rules" class="source-form" label-position="top">
      <el-form-item :label="$t('storagePage.groupName')" prop="groupName" class="form-input-item">
        <el-input :disabled="router.currentRoute.value.params.groupname" v-model="form.groupName" :placeholder="$t('storagePage.groupNamePlaceholder')"></el-input>
      </el-form-item>
      <el-form-item :label="$t('storagePage.groupDescription')" prop="description" class="form-input-item">
        <el-input v-model="form.description"></el-input>
      </el-form-item>
      <el-form-item :label="$t('storagePage.ttl')" class="form-input-item">
        <el-input v-model="form.ttl" type="number" class="ttl-input"></el-input>
        <el-select v-model="form.ttlUnit" class="ttl-input unit" placeholder="  ">
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
          pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/,
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
    });
    let form = reactive({
      groupName: '',
      description: '',
      ttl: '',
      ttlUnit: '',
    });
    /**
     * 取消新增存储组
     */
    const cancel = () => {
      props.func.removeTab(props.data.id);
      // props.func.addTab(router.currentRoute.value.params.serverid + 'connection');
    };
    /**
     * 获取存储组详情
     * serverid: 连接id
     * groupname:存储组名
     */
    const getGroupDetail = () => {
      axios.get(`/servers/${router.currentRoute.value.params.serverid}/storageGroups/${router.currentRoute.value.params.groupname}`, {}).then((res) => {
        if (res && res.code == 0) {
          form.groupName = res.data.groupName;
          form.ttl = res.data.ttl;
          form.ttlUnit = res.data.ttlUnit;
          form.description = res.data.description;
          alias.value = res.data.alias;
        }
      });
    };
    /**
     * 新增/编辑存储组
     */
    const submit = () => {
      formRef.value.validate((valid) => {
        if (valid) {
          if ((form.ttl && !form.ttlUnit) || (!form.ttl && form.ttlUnit)) {
            ElMessage.error(t('storagePage.ttlErrTips'));
            return false;
          }
          const reqObj = {
            groupName: form.groupName,
            description: form.description,
            ttl: form.ttl == '' || form.ttl == null ? null : +form.ttl,
            ttlUnit: form.ttlUnit || null,
          };
          axios.post(`/servers/${router.currentRoute.value.params.serverid}/storageGroups`, { ...reqObj }).then((res) => {
            if (res && res.code == 0) {
              ElMessage.success(t('sourcePage.newGroupSuccessLabel'));
              props.func.removeTab(props.data.id);
              props.func.updateTreeByIds([props.data.id]);
              props.func.expandByIds([props.data.id]);
              props.func.addTab(router.currentRoute.value.params.serverid + 'connection' + form.groupName + 'storageGroup');
            }
          });
        }
      });
    };
    /**
     * 单独获取数据源的alias
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
        console.log(route.params, 'update');
        form.groupName = null;
        form.ttl = null;
        form.ttlUnit = null;
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
    .ttl-input {
      display: inline-block;
      width: 350px;
    }
    .unit {
      width: 90px;
      margin-left: 10px;
    }
    .tips {
      color: rgba(34, 34, 34, 0.4);
      font-size: 12px;
    }
  }

  .submit-btns {
    .el-button {
      width: 110px;
      padding-left: 0 !important;
    }
  }
}
</style>
