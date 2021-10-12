<!-- 角色管理右部分 -->
<template>
  <div id="role-tabs">
    <el-tabs v-if="current.id!==null" v-model="activeTab" @tab-click="handleClick">
      <el-tab-pane  :label="$t('sourcePage.baseConfig')" name="baseConfig">
        <role-info :roleInfo="current"></role-info>
      </el-tab-pane>
      <template v-if="current.id">
        <el-tab-pane :label="$t('sourcePage.dataManagePrivilege')" name="dataManagePrivilege"> </el-tab-pane>
        <el-tab-pane :label="$t('sourcePage.permitPermission')" name="permitPermission"> </el-tab-pane>
      </template>
    </el-tabs>
    <div v-else class="no-data">暂无数据</div>
  </div>
</template>

<script>
import { useI18n } from 'vue-i18n';
import { ref } from 'vue';
import { ElTabs, ElTabPane } from 'element-plus';
import RoleInfo from './RoleInfo.vue';
export default {
  name: 'PowerManage',
  props: {
    current: {
      type: Object,
      default: () => {},
    },
  },
  setup() {
    const { t, locale } = useI18n();
    const activeTab = ref('baseConfig');
    const handleClick = () => {};

    return {
      t,
      locale,
      activeTab,
      handleClick,
    };
  },
  components: {
    ElTabs,
    ElTabPane,
    RoleInfo,
  },
};
</script>
<style scoped lang="scss">
#role-tabs {
  flex: 1;
  &:deep(.el-tabs) {
    margin-left: 20px;
    .el-tabs__header {
      margin-bottom: 4px;
      background-color: #f9fbfc;
    }
  }

  .no-data {
    color: #7a859e;
    padding: 20px;
    height: 100%;
    width: 100%;
    background-color: #f9fbfc;
    box-sizing: border-box;
    font-size: 12px;
  }
}
.el-tabs__content {
  margin-top: 4px;
  background-color: #f9fbfc;
  height: calc(100% - 4px);
}
</style>
