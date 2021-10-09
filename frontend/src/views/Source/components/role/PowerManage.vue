<!-- 角色管理右部分 -->
<template>
  <div id="role-tabs">
    <el-tabs v-model="activeTab" @tab-click="handleClick">
      <el-tab-pane :label="$t('sourcePage.baseConfig')" name="baseConfig">
        <role-info></role-info>
      </el-tab-pane>
      <template v-if="current.id">
        <el-tab-pane :label="$t('sourcePage.dataManagePrivilege')" name="dataManagePrivilege"> </el-tab-pane>
        <el-tab-pane :label="$t('sourcePage.permitPermission')" name="permitPermission"> </el-tab-pane>
      </template>
    </el-tabs>
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
      default: () => ({id:''}),
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
}
.el-tabs__content {
  margin-top: 4px;
  background-color: #f9fbfc;
  height: calc(100% - 4px);
}
</style>
