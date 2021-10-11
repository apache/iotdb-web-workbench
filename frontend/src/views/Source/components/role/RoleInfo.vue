<!-- 用户角色-基础信息 -->
<template>
  <el-form ref="roleForm" label-position="top" :model="form" :rules="rules" label-width="120px">
    <el-form-item :label="$t('sourcePage.roleName')" prop="roleName">
      <el-input v-model="form.roleName" type="text" autocomplete="off" maxLength="255" :placeholder="$t('sourcePage.inputRoleNameTip')"></el-input>
    </el-form-item>
    <el-form-item :label="$t('sourcePage.description')" prop="description">
      <el-input v-model="form.description" type="text" autocomplete="off" maxLength="100" :placeholder="$t('sourcePage.inputRoleDescTip')"></el-input>
    </el-form-item>
    <el-form-item :label="$t('sourcePage.grantUser')">
      <el-tag v-for="tag in form.users" :key="tag" closable type="success" size="small">
        {{ tag }}
      </el-tag>
      <svg class="icon" aria-hidden="true" @click="openGrantUserDialog">
        <use xlink:href="#icon-add1"></use>
      </svg>
    </el-form-item>
    <el-form-item>
      <el-button @click="resetForm('form')">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" @click="submitForm">{{ $t('common.submit') }}</el-button>
    </el-form-item>
    <dialog-grant-user ref="dialogGrantUser" :user-list="userList" @change="changeUser"></dialog-grant-user>
  </el-form>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import DialogGrantUser from './DialogGrantUser.vue';
import api from '../../api/index';
import { useRoute } from 'vue-router';

export default {
  components: { DialogGrantUser },
  name: 'RoleInfo',
  props: {
    roleInfo: {
      type: Object,
      default: () => {},
    },
  },
  computed: {},
  setup(props) {
    const { t, locale } = useI18n();

    let form = ref({
      roleName: '',
      description: '',
      users: [],
    });

    // console.log('props.roleInfo', props.roleInfo);
    // if (props.roleInfo.id) {
    //   // 编辑
    //   form.value = { ...props.roleInfo };
    //   console.log(props.roleInfo, form.value);
    // }

    watch(
      () => props.roleInfo,
      (val) => {
        if (val) {
          form.value = { ...val };
        }
      }
    );
    let userList = ref([]);

    let serverId = useRoute().params.serverid;
    let getUserList = async () => {
      let result = await api.getUsers(serverId);
      userList.value = result.data;
    };
    const rules = ref({
      roleName: [
        { required: true, min: 4, message: t(`sourcePage.roleNameLengthError`), trigger: 'blur' },
        {
          trigger: 'blur',
          // validator: async (rule, value, callback) => {
          // 重名检测
          // if (!/^([\u2E80-\u9FFF]|[a-zA-Z0-9])*$/.test(value)) {
          //   callback(new Error('名称不能包含特殊字符，由中文/英文/数字/符号组成'));
          //   return;
          // }
          // },
        },
      ],
    });
    let changeUser = ({ userList } = {}) => {
      form.value.users = userList;
    };
    const roleForm = ref(null);
    let submitForm = async () => {
      await roleForm.value.validate();
      let { roleName, description } = form.value;
      let payload = {
        roleName,
        description,
      };
      await api.editRole(serverId, payload);
    };

    onMounted(() => {
      getUserList();
    });
    return {
      t,
      locale,
      form,
      rules,
      userList,
      changeUser,
      submitForm,
      roleForm,
    };
  },
  methods: {
    openGrantUserDialog() {
      this.$refs.dialogGrantUser.open();
    },
  },
};
</script>
<style scoped lang="scss">
::v-deep .el-tag {
  margin-right: 10px;
}
.icon {
  cursor: pointer;
}
</style>
