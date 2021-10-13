<!-- 用户角色 -->
<template>
  <div class="user-role-wrapper">
    <role-list :server-id="serverId" @changeCurrRole="changeRole" @roleList="refreshList"></role-list>
    <power-manage :current="currentRole" :role-list="roleList"></power-manage>
  </div>
</template>

<script>
import { ref } from 'vue';
import RoleList from './RoleList.vue';
import PowerManage from './PowerManage.vue';
export default {
  name: '',
  components: {
    RoleList,
    PowerManage,
  },
  setup() {
    let currentRole = ref({});
    let roleList = ref([]);
    const changeRole = (role) => {
      currentRole.value = role;
    };

    const refreshList = (list) => {
      roleList.value = list;
    };
    return {
      changeRole,
      currentRole,
      refreshList,
      roleList,
    };
  },
};
</script>
<style lang="scss" scoped>
.user-role-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
}
</style>
