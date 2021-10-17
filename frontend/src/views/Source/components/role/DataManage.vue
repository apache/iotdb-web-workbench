<!-- 角色数据管理权限 -->
<template>
  <div class="data-manage">
    <el-button type="primary" @click="addPermit">添加权限</el-button>
    <permit-dialog ref="permitDialogRef" @submit="submitPermit"></permit-dialog>
  </div>
</template>

<script>
import { ref } from 'vue';
import PermitDialog from '../permitDialog';
import api from '../../api/index';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';

export default {
  name: 'DataManage',
  props: {
    roleInfo: {
      type: Object,
      default: () => {},
    },
  },
  setup(props) {
    let permitDialogRef = ref(null);
    let serverId = useRoute().params.serverid;
    let oldValue = ref({});
    const addPermit = () => {
      permitDialogRef.value.open({ type: 'add', origin: 'role' });
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
      payload.privileges = dealPivilege.addList;
      payload.cancelPrivileges = dealPivilege.deleteList;
      // 处理存储组
      if (type === 1) {
        let dealGroup = handlePath('groupPaths', range);
        payload.groupPaths = dealGroup.addList;
        payload.delGroupPaths = dealGroup.deleteList;
      }
      // 处理实体
      if (type === 2) {
        let dealGroup = handlePath('groupPaths', [range.storage]);
        payload.groupPaths = dealGroup.addList;
        payload.delGroupPaths = dealGroup.deleteList;
        
        let dealDevice = handlePath('devicePaths', range.device);
        payload.devicePaths = dealDevice.addList;
        payload.delDevicePaths = dealGroup.deleteList;
      }
      // 处理物理量
      if (type === 3) {
        let dealGroup = handlePath('groupPaths', [range.storage]);
        payload.groupPaths = dealGroup.addList;
        payload.delGroupPaths = dealGroup.deleteList;
        
        let dealDevice = handlePath('devicePaths', [range.device]);
        payload.devicePaths = dealDevice.addList;
        payload.delDevicePaths = dealGroup.deleteList;

        let timeDevice = handlePath('timeseriesPaths', range.time);
        payload.timeseriesPaths = timeDevice.addList;
        payload.delTimeseriesPaths = timeDevice.deleteList;
      }
      if (dialogType === 'add') {
        api.editDataPrivilege(params, payload);
        permitDialogRef.value.visible = false;
        ElMessage.success('新增权限成功');
      }
    };
    const handlePath = (props, List) => {
      let deleteList = oldValue?.value[props]?.filter((d) => !List.includes(d));
      let addList = List.filter((d) => !oldValue?.value[props]?.includes(d));
      return {
        addList,
        deleteList,
      };
    };
    return {
      permitDialogRef,
      addPermit,
      submitPermit,
    };
  },
  components: {
    PermitDialog,
  },
};
</script>
<style scoped lang="scss"></style>
