<!-- 用户角色 -->
<template>
  <div class="user-role-wrapper">
    <role-list @change="changeRole"></role-list>
    <power-manage :current="currentRole"></power-manage>
  </div>
</template>

<script>
import { ref } from 'vue';
import RoleList from './RoleList.vue';
import PowerManage from './PowerManage.vue';
export default {
  name: '',
  props: [],
  components: {
    RoleList,
    PowerManage,
  },
  setup() {
    let currentRole = ref({});
    const changeRole = (role) => {
      currentRole.value = role;
    };
    return {
      changeRole,
      currentRole
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
