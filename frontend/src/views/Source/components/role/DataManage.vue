<!-- 角色数据管理权限 -->
<template>
  <div class="data-manage">
    <el-button type="primary" @click="addPermit">添加权限</el-button>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column show-overflow-tooltip :label="$t('sourcePage.path')" width="180">
        <template #default="{ row }">
          <span>{{ pathMap[row.type] }}</span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('sourcePage.range')">
        <template #default="scope">
          <div v-if="scope.row.type == 0">-</div>
          <div v-else-if="scope.row.type == 1">
            <p>{{ $t('sourcePage.groupNameLabel') }}</p>
            <span v-for="item in scope.row.groupPaths" :key="item" class="device-path">{{ item }}</span>
          </div>
          <div v-else-if="scope.row.type == 2">
            <p>{{ $t('sourcePage.groupNameLabel') }}</p>
            <span class="device-path">{{ scope.row.groupPaths[0] }}</span>
            <p>{{ $t('sourcePage.deviceNameLabel') }}</p>
            <span v-for="item in scope.row.devicePaths" :key="item" class="device-path">{{ item }}</span>
          </div>
          <div v-else-if="scope.row.type == 3">
            <p>{{ $t('sourcePage.groupNameLabel') }}</p>
            <span class="device-path">{{ scope.row.groupPaths[0] }}</span>
            <p>{{ $t('sourcePage.deviceNameLabel') }}</p>
            <span class="device-path">{{ scope.row.devicePaths[0] }}</span>
            <p>{{ $t('sourcePage.timeNameLabel') }}</p>
            <span v-for="item in scope.row.timeseriesPaths" :key="item" class="device-path">{{ item }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('sourcePage.func')" width="300px">
        <template #default="scope">
          <span class="privilege-item" v-for="item in scope.row.privileges" :key="item">
            {{ privilegeMap[item] }}
            <span class="divide"></span>
          </span>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.operation')">
        <template #default="{ row }">
          <el-button type="text" size="small" @click="editPrivilege(row)">{{ $t('common.edit') }}</el-button>
          <el-popconfirm placement="top" :title="$t('sourcePage.deleteAuthConfirm')" @confirm="deletePrivilege(scope)">
            <template #reference>
              <el-button type="text" size="small" class="el-button-delete">{{ $t('common.delete') }}</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <permit-dialog ref="permitDialogRef" @submit="submitPermit"></permit-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import PermitDialog from '../permitDialog';
import api from '../../api/index';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';

export default {
  name: 'DataManage',
  props: {
    roleInfo: {
      type: Object,
      default: () => {},
    },
  },
  setup(props) {
    const { t, locale } = useI18n();
    let permitDialogRef = ref(null);
    let serverId = useRoute().params.serverid;
    let oldValue = ref({});
    let tableData = ref([]);

    let pathMap = ref({
      0: t('sourcePage.selectAlias'),
      1: t('sourcePage.selectGroup'),
      2: t('sourcePage.selectDevice'),
      3: t('sourcePage.selectTime'),
    });
    let privilegeMap = ref({
      SET_STORAGE_GROUP: t('sourcePage.createGroup'),
      CREATE_TIMESERIES: t('sourcePage.createTimeSeries'),
      INSERT_TIMESERIES: t('sourcePage.insertTimeSeries'),
      READ_TIMESERIES: t('sourcePage.readTimeSeries'),
      DELETE_TIMESERIES: t('sourcePage.deleteTimeSeries'),
    });
    let dataPrivileges = ref({
      0: [
        { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
        {
          id: 'CREATE_TIMESERIES',
          label: t('sourcePage.createTimeSeries'),
        },
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
      1: [
        { id: 'SET_STORAGE_GROUP', label: t('sourcePage.createGroup') },
        {
          id: 'CREATE_TIMESERIES',
          label: t('sourcePage.createTimeSeries'),
        },
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
      2: [
        {
          id: 'CREATE_TIMESERIES',
          label: t('sourcePage.createTimeSeries'),
        },
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
      3: [
        {
          id: 'INSERT_TIMESERIES',
          label: t('sourcePage.insertTimeSeries'),
        },
        { id: 'READ_TIMESERIES', label: t('sourcePage.readTimeSeries') },
        {
          id: 'DELETE_TIMESERIES',
          label: t('sourcePage.deleteTimeSeries'),
        },
      ],
    });
    const addPermit = () => {
      permitDialogRef.value.open({ type: 'add' });
    };

    const editPrivilege = (row) => {
      oldValue.value = row;
      permitDialogRef.value.open({ type: 'edit', data: row });
    };
    const submitPermit = ({ type, privileges, dialogType, range } = {}) => {
      console.log(type, privileges, dialogType, range);
      let payload = { type };
      let params = {
        serverId,
        roleName: props.roleInfo.roleName,
      };
      // 处理权限
      let dealPivilege = handlePath('privileges', privileges);
      payload.privileges = privileges;
      payload.cancelPrivileges = dealPivilege.deleteList;
      // 处理存储组
      if (type === 1) {
        let dealGroup = handlePath('groupPaths', range);
        payload.groupPaths = dealGroup.addList;
      }
      // 处理实体
      if (type === 2) {
        payload.groupPaths = [range.storage];
        payload.devicePaths = range.device;
      }
      // 处理物理量
      if (type === 3) {
        payload.groupPaths = [range.storage];
        payload.devicePaths = [range.device];
        payload.timeseriesPaths = range.time;
      }
      api.editDataPrivilege(params, payload);
      permitDialogRef.value.visible = false;
      ElMessage.success(`${dialogType === 'add' ? '新增' : '编辑'}权限成功`);
      getData();
    };
    const getData = async () => {
      let result = await api.getDataPrivilege({ serverId, roleName: props.roleInfo.roleName });
      tableData.value = result.data;
    };
    const deletePrivilege = () => {};
    const handlePath = (props, List) => {
      let deleteList = oldValue?.value[props]?.filter((d) => !List.includes(d));
      let addList = List.filter((d) => !oldValue?.value[props]?.includes(d));
      return {
        addList,
        deleteList,
      };
    };
    onMounted(() => {
      getData();
    });
    return {
      t,
      locale,
      permitDialogRef,
      addPermit,
      submitPermit,
      pathMap,
      tableData,
      editPrivilege,
      deletePrivilege,
      dataPrivileges,
      privilegeMap,
    };
  },
  components: {
    PermitDialog,
  },
};
</script>
<style scoped lang="scss">
.data-manage {
  overflow: auto;
  &:deep(.el-button) {
    margin-bottom: 15px;
  }
}
.privilege-item {
  display: flex;
  align-items: center;
  line-height: 24px;

  .divide {
    background: $border-color;
    width: 1px;
    margin: 0 10px;
    height: 16px;
  }
}
.show-only {
  &:deep(.el-checkbox) {
    display: none;
  }
  &:deep(.is-checked) {
    display: inline-block !important;
    .is-checked {
      display: none !important;
    }
  }
  &:deep(.el-checkbox__input) {
    display: none;
  }
}
</style>
