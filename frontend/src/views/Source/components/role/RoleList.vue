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
      <li v-for="item in roleList" :key="item" class="role-list-item" @click="clickRole(item)">
        <div :class="[activeRole === item ? 'circle active-circle' : 'circle']">
          <div class="small-circle"></div>
        </div>
        {{ item }}
        <div class="operate">
          <svg class="icon" aria-hidden="true">
            <use xlink:href="#icon-se-icon-f-edit"></use>
          </svg>
          <svg class="icon delete" aria-hidden="true">
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
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api from '../../api/index';

export default {
  name: 'RoleList',
  props: [],
  setup(props, context) {
    const { t, locale } = useI18n();
    let roleList = ref([]);

    let serverId = useRoute().params.serverid;
    // 获取所有角色
    let getRoleList = async () => {
      let result = await api.getRoles(serverId);
      roleList.value = result.data;
      if (!roleList.value.length) {
        context.emit('change', { id: null });
      }
      activeRole.value = result?.data[0];
    };

    let activeRole = ref(1);

    const addRole = () => {
      roleList.value.unshift('NEW');
      activeRole.value = 'NEW';
      context.emit('change', { id: '', name: 'NEW' });
    };
    const clickRole = async (item) => {
      activeRole.value = item;
      let roleInfo = await api.getRoleInfo({ serverId, roleName: item });
      context.emit('change', { ...roleInfo.data, roleName: item });
    };

    onMounted(() => {
      getRoleList();
    });

    return {
      t,
      locale,
      roleList,
      getRoleList,
      activeRole,
      addRole,
      clickRole,
    };
  },
};
</script>
<style scoped lang="scss">
.list-wrapper {
  flex-basis: 280px;
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

      &:hover {
        background: #ffffff;
        color: #333333;
        border-radius: 30px 0px 0px 30px;
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
