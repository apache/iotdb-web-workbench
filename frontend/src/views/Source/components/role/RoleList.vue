<!-- 用户缴了-角色列表 -->
<template>
  <div class="list-wrapper">
    <div class="user-title">
      <span class="title">{{ $t('sourcePage.roleList') }}</span>
      <div class="btn">
        <svg class="icon" aria-hidden="true" @click="addRole">
          <use xlink:href="#icon-add1"></use>
        </svg>
      </div>
    </div>
    <ul v-if="roleList.length" class="role-list">
      <li v-for="item in roleList" :key="item" :class="[activeRole === item ? 'active-item' : '']" class="role-list-item" @click="clickRole(item)">
        <div :class="[activeRole === item ? 'circle active-circle' : 'circle']">
          <div class="small-circle"></div>
        </div>
        {{ item }}
        <div class="operate">
          <svg class="icon" aria-hidden="true" v-if="!isAdding && activeRole === item" @click.stop="editRole(item)">
            <use xlink:href="#icon-se-icon-f-edit"></use>
          </svg>
          <svg v-if="activeRole === item" class="icon delete" aria-hidden="true" @click.stop="deleteRole(item)">
            <use xlink:href="#icon-se-icon-delete"></use>
          </svg>
        </div>
      </li>
    </ul>
    <div v-else class="no-data">暂无数据</div>
  </div>
</template>

<script>
import { useI18n } from 'vue-i18n';
import { ref, onMounted, getCurrentInstance, onUnmounted, watchEffect } from 'vue';
import { useRoute } from 'vue-router';
import api from '../../api/index';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useStore } from 'vuex';

export default {
  name: 'RoleList',
  props: [],
  setup(props, { emit }) {
    const store = useStore();
    let canPrivilege = {};
    watchEffect(() => {
      canPrivilege = store.getters.canPrivilege;
    });
    const { t, locale } = useI18n();
    let roleList = ref([]);
    let activeRole = ref(1);
    let serverId = useRoute().params.serverid;
    const emitter = getCurrentInstance().appContext.config.globalProperties.emitter;
    let isAdding = ref(false);

    // 获取所有角色
    let getRoleList = async (roleName) => {
      isAdding.value = false;
      let result = await api.getRoles(serverId);
      roleList.value = result.data;
      if (!roleList.value.length) {
        emit('changeCurrRole', { id: '', name: 'NEW' });
      } else {
        clickRole(roleName ? roleName : result?.data[0]);
      }
      emit('roleList', roleList.value);
    };

    const addRole = () => {
      if (isAdding.value) {
        ElMessage.error('请先完成新增操作');
        return;
      }
      if (!canPrivilege.canAddRole) {
        ElMessage.error(t('sourcePage.noAuthTip'));
        return;
      }
      roleList.value.unshift('NEW');
      activeRole.value = 'NEW';
      emit('changeCurrRole', { id: '', name: 'NEW', type: 'add' });
      isAdding.value = true;
    };
    const clickRole = async (item) => {
      if (isAdding.value) {
        ElMessage.error('请先完成新增操作');
        return;
      }
      activeRole.value = item;
      let roleInfo = await api.getRoleInfo({ serverId, roleName: item });
      let privileges = await api.getAuthPrivilege({ serverId, roleName: item });
      emit('changeCurrRole', { ...roleInfo.data, roleName: item, privileges: privileges.data, type: 'view' });
    };
    const editRole = async (item) => {
      if (isAdding.value) {
        ElMessage.error('请先完成新增操作');
        return;
      }
      activeRole.value = item;
      let roleInfo = await api.getRoleInfo({ serverId, roleName: item });
      let privileges = await api.getAuthPrivilege({ serverId, roleName: item });
      emit('changeCurrRole', { ...roleInfo.data, roleName: item, privileges: privileges.data, type: 'edit' });
    };
    const deleteRole = async (item) => {
      if (isAdding.value) {
        ElMessage.error('请先完成新增操作');
        return;
      }
      if (!canPrivilege.canDeleteRole) {
        ElMessage.error(t('sourcePage.noAuthTip'));
        return;
      }
      await ElMessageBox.confirm('确认删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        await api.deleteRole({ serverId, roleName: item });
        ElMessage.success('删除成功');
        getRoleList();
      });
    };
    const cancelAdd = () => {
      if (isAdding.value) {
        roleList.value.splice(0, 1);
        isAdding.value = false;
        clickRole(roleList?.value[0]);
      }
    };

    onMounted(() => {
      getRoleList();
      emitter.on('add-role', getRoleList);
      emitter.on('cancel-add-role', cancelAdd);
    });
    onUnmounted(() => {
      emitter.off('add-role', getRoleList);
      emitter.off('cancel-add-role', cancelAdd);
    });

    return {
      t,
      locale,
      roleList,
      getRoleList,
      activeRole,
      addRole,
      clickRole,
      deleteRole,
      editRole,
      cancelAdd,
    };
  },
  methods: {},
};
</script>
<style scoped lang="scss">
.list-wrapper {
  flex-basis: 220px;
  flex-grow: 0;
  flex-shrink: 0;
  height: 100%;
  font-size: 12px;
  text-align: center;

  .no-data {
    color: #7a859e;
    padding-top: 30px;
    height: calc(100% - 44px);
    width: 100%;
    background-color: #f9fbfc;
    margin-top: 4px;
    box-sizing: border-box;
  }

  .user-title {
    padding: 10px 20px;
    display: flex;
    justify-content: space-between;
    background-color: #f9fbfc;
    color: #808ba3;
    height: 40px;
    align-items: center;
    box-sizing: border-box;
    .btn {
      font-size: 14px;
      cursor: pointer;
    }
  }
  .role-list {
    background-color: #f9fbfc;
    margin-top: 4px;
    padding: 10px 0 10px 10px;
    box-sizing: border-box;
    color: #7a859e;
    height: calc(100% - 44px);
    overflow: auto;
    .active-item {
      background: #ffffff;
    }
    &-item {
      width: 100%;
      transition: all 0.2s;
      height: 40px;
      line-height: 20px;
      padding: 10px;
      box-sizing: border-box;
      display: flex;
      position: relative;
      cursor: pointer;
      border-radius: 30px 0px 0px 30px;

      &:hover {
        background: #ffffff;
        color: #333333;
        .circle {
          background: #ffffff;
          .small-circle {
            background: #ffffff;
          }
        }
      }

      .circle {
        width: 20px;
        height: 20px;
        background: #f9fbfc;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 10px;
        .small-circle {
          width: 6px;
          height: 6px;
          background: #f9fbfc;
          border-radius: 50%;
          position: absolute;
        }
        &.active-circle {
          background: #edf8f5;
          .small-circle {
            background: #13c393;
          }
        }
      }

      .operate {
        position: absolute;
        right: 10px;
        .icon {
          cursor: pointer;
        }
        .delete {
          margin-left: 15px;
        }
      }
    }
  }
}
</style>
