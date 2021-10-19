<!-- 用户角色-基础信息 -->
<template>
  <el-form ref="roleForm" label-position="top" :model="form" :rules="rules" label-width="120px">
    <el-form-item :label="$t('sourcePage.roleName')" prop="roleName">
      <el-input :disabled="stateType === 'view'" v-model="form.roleName" type="text" autocomplete="off" maxLength="255" :placeholder="$t('sourcePage.inputRoleNameTip')"></el-input>
    </el-form-item>
    <el-form-item :label="$t('sourcePage.description')" prop="description">
      <el-input :disabled="stateType === 'view'" v-model="form.description" type="text" autocomplete="off" maxLength="100" :placeholder="$t('sourcePage.inputRoleDescTip')"></el-input>
    </el-form-item>
    <el-form-item :label="$t('sourcePage.grantUser')">
      <el-tag v-for="tag in form.users" :key="tag" :closable="roleInfo.type === 'edit'" size="small" class="el-tag-deep-green" @close="closeTag(tag)">
        {{ tag }}
      </el-tag>
      <svg v-if="stateType === 'edit' || stateType === 'add'" class="icon" aria-hidden="true" @click="openGrantUserDialog">
        <use xlink:href="#icon-add1"></use>
      </svg>
    </el-form-item>
    <el-form-item v-if="stateType === 'edit' || stateType === 'add'">
      <el-button @click="resetForm">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" @click="submitForm">{{ $t('common.submit') }}</el-button>
    </el-form-item>
    <dialog-grant-user ref="dialogGrantUser" :user-list="userList" @change="changeUser"></dialog-grant-user>
  </el-form>
</template>

<script>
import { ref, onMounted, watch, getCurrentInstance, computed } from 'vue';
import { useStore } from 'vuex';
import { useI18n } from 'vue-i18n';
import DialogGrantUser from './DialogGrantUser.vue';
import api from '../../api/index';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';

export default {
  components: { DialogGrantUser },
  name: 'RoleInfo',
  props: {
    roleInfo: {
      type: Object,
      default: () => {},
    },
    roleList: {
      type: Array,
      default: () => [],
    },
  },
  setup(props) {
    const { t, locale } = useI18n();
    const store = useStore();
    const internalInstance = getCurrentInstance();
    const emitter = internalInstance.appContext.config.globalProperties.emitter;
    let stateType = ref();
    let form = ref({
      roleName: '',
      description: '',
      users: [],
    });

    let oldForm = ref({});
    const roleForm = ref(null);
    let userList = ref([]);
    let serverId = useRoute().params.serverid;

    watch(
      () => props.roleInfo,
      (val) => {
        if (val) {
          form.value = { ...val };
          form.value.users = val.userList;
          oldForm.value = { ...form.value };
          roleForm.value.clearValidate();
          stateType.value = props.roleInfo.type;
        }
      }
    );
    const rules = ref({
      roleName: [
        { required: true, min: 4, message: t(`sourcePage.roleNameLengthError`), trigger: 'blur' },
        {
          trigger: 'blur',
          validator: async (rule, value, callback) => {
            let reg = /^[0-9]*$/;
            if (reg.test(value)) {
              callback(new Error('名称不能为纯数字'));
            }
            // 重名检测
            if (oldForm?.value?.id && oldForm?.value?.roleName !== value) {
              if (props.roleList.includes(value)) {
                callback(new Error('名称已存在'));
              }
            } else if (!oldForm?.value?.id && props.roleList.includes(value)) {
              callback(new Error('名称已存在'));
            }
            callback();
          },
        },
      ],
    });

    let isEdit = computed(() => {
      return oldForm.value.id;
    });
    let getUserList = async () => {
      let result = await api.getUsers(serverId);
      userList.value = result.data;
    };
    let changeUser = ({ userList } = {}) => {
      form.value.users = userList;
    };
    let closeTag = (tag) => {
      form.value.users = form.value.users.filter((d) => d !== tag);
    };
    let submitForm = async () => {
      await roleForm.value.validate();
      let { roleName, description } = form.value;
      let payload = {
        roleName,
        description,
      };
      if (isEdit.value) {
        payload.id = isEdit.value;
      }
      try {
        await api.editRole(serverId, payload);
        let newUsers = form.value.users;
        let deleteUser = oldForm?.value.userList?.filter((d) => !newUsers.includes(d));
        let addUser = newUsers?.filter((d) => !oldForm?.value.userList?.includes(d));
        try {
          if (addUser?.length || deleteUser?.length) {
            await api.grantUserRole({ serverId, roleName }, { cancelUserList: deleteUser, userList: addUser });
          }
          ElMessage.success(`角色${isEdit.value ? '编辑' : '创建'}成功`);
          emitter.emit('add-role', isEdit.value ? roleName : '');
        } catch (e) {
          //
        }
      } catch (e) {
        //
      }
    };
    const resetForm = () => {
      if (oldForm.value.id) {
        form.value = { ...oldForm.value };
      } else {
        emitter.emit('cancel-add-role');
      }
    };
    onMounted(() => {
      getUserList();
    });
    return {
      oldForm,
      t,
      locale,
      form,
      rules,
      userList,
      changeUser,
      submitForm,
      roleForm,
      stateType,
      store,
      closeTag,
      resetForm,
    };
  },
  methods: {
    openGrantUserDialog() {
      this.$refs.dialogGrantUser.open(this.form.users);
    },
  },
};
</script>
<style scoped lang="scss">
::v-deep .el-tag {
  margin-right: 10px;
}
.el-input {
  width: 280px;
}
.icon {
  cursor: pointer;
}
</style>
