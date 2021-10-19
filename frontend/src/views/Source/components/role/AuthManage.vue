<!-- 权限管理权限 -->
<template>
  <div class="permitpermission-content">
    <p class="tips">{{ $t('sourcePage.permitTips') }}</p>
    <div class="permit-list">
      <div class="permit-list-type">
        <div class="box box1">
          <el-checkbox v-model="allChecked.user" :indeterminate="user" :label="$t('sourcePage.userRelevance')" @change="handleCheckAllChange('user')"></el-checkbox>
        </div>
        <el-checkbox-group v-model="checked.user" class="wraper" @change="handleItemCheckedChange($event, 'user')">
          <el-checkbox v-for="item in relationList.user" :label="item.id" :key="item.id">{{ item.label }}</el-checkbox>
        </el-checkbox-group>
      </div>
      <div class="permit-list-type">
        <div class="box box2">
          <el-checkbox v-model="allChecked.role" :indeterminate="role" :label="$t('sourcePage.roleRelevance')" @change="handleCheckAllChange('role')"></el-checkbox>
        </div>
        <el-checkbox-group v-model="checked.role" @change="handleItemCheckedChange($event, 'role')">
          <el-checkbox v-for="item in relationList.role" :label="item.id" :key="item.id" @change="changeItemCheck">{{ item.label }}</el-checkbox>
        </el-checkbox-group>
      </div>
      <div class="permit-list-type">
        <div class="box box3">
          <el-checkbox v-model="allChecked.udf" :indeterminate="udf" :label="$t('sourcePage.udf')"  @change="handleCheckAllChange('udf')"></el-checkbox>
        </div>
        <el-checkbox-group v-model="checked.udf" @change="handleItemCheckedChange($event, 'udf')">
          <el-checkbox v-for="item in relationList.udf" :label="item.id" :key="item.id">{{ item.label }}</el-checkbox>
        </el-checkbox-group>
      </div>
      <div class="permit-list-type">
        <div class="box box4">
          <el-checkbox v-model="allChecked.trigger" :indeterminate="trigger" :label="$t('sourcePage.trigger')" @change="handleCheckAllChange('trigger')"></el-checkbox>
        </div>
        <el-checkbox-group v-model="checked.trigger" @change="handleItemCheckedChange($event, 'trigger')">
          <el-checkbox v-for="item in relationList.trigger" :label="item.id" :key="item.id">{{ item.label }}</el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
    <div class="permit-btn">
      <el-button size="small" @click="cancel">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" size="small" @click="submit">{{ $t('common.save') }}</el-button>
    </div>
  </div>
</template>

<script>
import { useI18n } from 'vue-i18n';
import { ref, reactive, toRefs, computed, watch } from 'vue';
import api from '../../api/index';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
export default {
  name: 'AuthManage',
  props: {
    // 当前角色
    roleInfo: {
      type: Object,
      default: () => {},
    },
    // 登录的账号
    baseInfo: {
      type: Object,
      default: () => {},
    },
  },
  setup(props) {
    const { t, locale } = useI18n();
    let serverId = useRoute().params.serverid;
    let oldPrivileges = ref([]);
    let allChecked = ref({
      user: false,
      role: false,
      udf: false,
      trigger: false,
    });

    let checked = ref({
      user: [],
      role: [],
      udf: [],
      trigger: [],
    });

    let isIndeterminate = reactive({
      user: false,
      role: false,
      udf: false,
      trigger: false,
    });

    let relationList = computed(() => {
      return {
        user: [
          {
            id: 'LIST_USER',
            label: t('sourcePage.listUser'),
          },
          {
            id: 'CREATE_USER',
            label: t('sourcePage.createUser'),
          },
          { id: 'DELETE_USER', label: t('sourcePage.deleteUser') },
          { id: 'MODIFY_PASSWORD', label: t('sourcePage.editPassword') },
          {
            id: 'GRANT_USER_PRIVILEGE',
            label: t('sourcePage.grantPrivilege'),
          },
          {
            id: 'REVOKE_USER_PRIVILEGE',
            label: t('sourcePage.revertPrivilege'),
          },
          {
            id: 'GRANT_USER_ROLE',
            label: t('sourcePage.grantUserRole'),
          },
          {
            id: 'REVOKE_USER_ROLE',
            label: t('sourcePage.revokeUserRole'),
          },
        ],
        role: [
          {
            id: 'LIST_ROLE',
            label: t('sourcePage.listRole'),
          },

          { id: 'CREATE_ROLE', label: t('sourcePage.createRole') },
          { id: 'DELETE_ROLE', label: t('sourcePage.deleteRole') },
          {
            id: 'GRANT_ROLE_PRIVILEGE',
            label: t('sourcePage.grantRolePrivilege'),
          },
          {
            id: 'REVOKE_ROLE_PRIVILEGE',
            label: t('sourcePage.revertRolePrivilege'),
          },
        ],
        udf: [
          {
            id: 'CREATE_FUNCTION',
            label: t('sourcePage.createFunction'),
          },
          {
            id: 'DROP_FUNCTION',
            label: t('sourcePage.uninstallFunction'),
          },
        ],
        trigger: [
          { id: 'CREATE_TRIGGER', label: t('sourcePage.createTrigger') },
          { id: 'DROP_TRIGGER', label: t('sourcePage.uninstallTrigger') },
          { id: 'START_TRIGGER', label: t('sourcePage.startTrigger') },
          { id: 'STOP_TRIGGER', label: t('sourcePage.stopTrigger') },
        ],
      };
    });

    let allPrivileges = computed(() => {
      return {
        user: relationList.value.user.map((d) => d.id),
        role: relationList.value.role.map((d) => d.id),
        udf: relationList.value.udf.map((d) => d.id),
        trigger: relationList.value.trigger.map((d) => d.id),
      };
    });

    // 更新全选状态
    let resetAllChecked = (type) => {
      const checkedCount = checked.value[type].length;
      isIndeterminate[type] = checked.value[type].length > 0 && checked.value[type].length < relationList.value[type].length;
      allChecked.value[type] = checkedCount === relationList.value[type].length;
    };

    watch(
      () => props.roleInfo.privileges,
      () => {
        let privileges = props.roleInfo.privileges;
        oldPrivileges.value = privileges;
        checked.value = {
          user: [],
          role: [],
          udf: [],
          trigger: [],
        };
        let typeArr = ['user', 'role', 'udf', 'trigger'];
        privileges.forEach((e) => {
          typeArr.forEach((i) => {
            if (allPrivileges.value[i].includes(e)) {
              checked.value[i].push(e);
            }
          });
        });
        typeArr.forEach((e) => resetAllChecked(e));
      },
      { immediate: true }
    );

    let cancel = () => {
      let typeArr = ['user', 'role', 'udf', 'trigger'];
      checked.value = {
        user: [],
        role: [],
        udf: [],
        trigger: [],
      };
      oldPrivileges.value.forEach((e) => {
        typeArr.forEach((i) => {
          if (allPrivileges.value[i].includes(e)) {
            checked.value[i].push(e);
          }
        });
      });
      typeArr.forEach((e) => resetAllChecked(e));
    };
    let submit = () => {
      let newPrivileges = Object.values(checked.value).reduce((total, curr) => total.concat(curr));
      let cancelPrivileges = oldPrivileges?.value?.filter((d) => !newPrivileges.includes(d));
      let addPrivileges = newPrivileges?.filter((d) => !oldPrivileges?.value.includes(d));
      try {
        api.editAuthPrivilege({ serverId, roleName: props.roleInfo.roleName }, { cancelPrivileges, privileges: addPrivileges });
        oldPrivileges.value = newPrivileges;
        ElMessage.success('编辑成功');
      } catch (e) {
        //
      }
    };

    let handleCheckAllChange = (type) => {
      // 如果全选
      if (allChecked.value[type]) {
        checked.value[type] = relationList.value[type].map((d) => d.id);
      } else {
        checked.value[type] = [];
      }
      resetAllChecked(type);
    };

    let handleItemCheckedChange = (event, type) => {
      checked.value[type] = event;
      resetAllChecked(type);
    };
    return {
      t,
      locale,
      allChecked,
      allPrivileges,
      relationList,
      cancel,
      submit,
      checked,
      ...toRefs(isIndeterminate),
      handleCheckAllChange,
      handleItemCheckedChange,
      resetAllChecked,
    };
  },
};
</script>
<style scoped lang="scss">
.permitpermission-content {
  height: 100%;
  .tips {
    color: #fb5151ff;
    font-size: 12px;
    margin-bottom: 16px;
  }
  .wraper {
  }
  .permit-list {
    display: flex;
    height: calc(100% - 60px);

    .permit-list-type {
      flex: 1;
      background: #ffffff;
      border-radius: 4px;
      border: 1px solid #eaecf0;
      margin-right: 20px;
      padding: 16px;
      overflow: auto;
      &:last-child {
        margin-right: 0;
      }
      .el-checkbox {
        padding-left: 10px;
        height: 32px;
        line-height: 32px;
        display: block;
      }
      .box {
        border-radius: 4px;
        height: 40px;
        line-height: 40px;
        padding-left: 10px;
        .el-checkbox {
          padding-left: 0;
          height: 40px;
          line-height: 40px;
        }
      }
      .box1 {
        background: #fff9f3;
      }
      .box2 {
        background: #f3fbff;
      }
      .box3 {
        background: #fff3f3;
      }
      .box4 {
        background: #f2fff6;
      }
    }
  }
  .permit-btn {
    text-align: center;
    margin-top: 10px;
  }
}
</style>
