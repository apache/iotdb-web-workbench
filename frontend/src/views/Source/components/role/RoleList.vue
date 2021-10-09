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
    <ul class="role-list">
      <li v-for="item in roleList" :key="item.id" class="role-list-item" @click="clickRole(item)">
        <div :class="[activeRole === item.id ? 'circle active-circle' : 'circle']">
          <div class="small-circle"></div>
        </div>
        {{ item.name }}
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
  </div>
</template>

<script>
import { useI18n } from 'vue-i18n';
import { ref } from 'vue';

export default {
  name: 'RoleList',
  props: [],
  setup(props, context) {
    const { t, locale } = useI18n();
    const roleList = ref([
      { name: '111', id: 111 },
      { name: '1112', id: 1112 },
      { name: '333', id: 333 },
    ]);
    let activeRole = ref(111);

    const addRole = () => {
      roleList.value.unshift({ id: '', name: 'NEW' });
      activeRole.value = '';
      context.emit('change', { id: '', name: 'NEW' });
    };
    const clickRole = (item) => {
      activeRole.value = item.id;
      context.emit('change', { ...item });
    }

    return {
      t,
      locale,
      roleList,
      activeRole,
      addRole,
      clickRole
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
