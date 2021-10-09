<!-- 用户角色-基础信息 -->
<template>
  <el-form ref="form" label-position="top" :model="form" :rules="rules" label-width="120px">
    <el-form-item :label="$t('sourcePage.roleName')" prop="roleName">
      <el-input v-model="form.pass" type="password" autocomplete="off" maxLength="255" :placeholder="$t('sourcePage.inputRoleNameTip')"></el-input>
    </el-form-item>
    <el-form-item :label="$t('sourcePage.description')" prop="description">
      <el-input v-model="form.checkPass" type="password" autocomplete="off" maxLength="100" :placeholder="$t('sourcePage.inputRoleDescTip')"></el-input>
    </el-form-item>
    <el-form-item :label="$t('sourcePage.grantUser')" prop="description">
      <el-tag v-for="tag in userList" :key="tag.name" closable type="success" size="small">
        {{ tag.name }}
      </el-tag>
      <svg class="icon" aria-hidden="true" @click="openGrantUserDialog">
        <use xlink:href="#icon-add1"></use>
      </svg>
    </el-form-item>
    <el-form-item>
      <el-button @click="resetForm('form')">{{ $t('common.cancel') }}</el-button>
      <el-button type="primary" @click="submitForm('form')">{{ $t('common.submit') }}</el-button>
    </el-form-item>
    <dialog-grant-user ref="dialogGrantUser"></dialog-grant-user>
  </el-form>
</template>

<script>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import DialogGrantUser from './DialogGrantUser.vue';
export default {
  components: { DialogGrantUser },
  name: 'RoleInfo',
  props: [],
  computed: {},
  setup() {
    const { t, locale } = useI18n();
    let form = ref({
      roleName: '',
      description: '',
    });
    let userList = ref([
      {
        id: 11,
        name: '123',
      },
      { id: 22, name: '123' },
      { id: 33, name: '333' },
    ]);
    const rules = ref({
      roleName: [
        { required: true, min: 4, message: t(`sourcePage.roleNameLengthError`), trigger: 'blur' },
        {
          trigger: 'blur',
          // validator: async (rule, value, callback) => {
          // 重名检测
          // if (!/^([\u2E80-\u9FFF]|[a-zA-Z0-9])*$/.test(value)) {
          //   callback(new Error('名称不能包含特殊字符，由中文/英文/数字/符号组成'));
          //   return;
          // }
          // },
        },
      ],
    });

    return {
      t,
      locale,
      form,
      rules,
      userList,
    };
  },
  methods: {
    async submitForm() {
      await this.$refs.form.validate();
    },
    openGrantUserDialog() {
      this.$refs.dialogGrantUser.open();
    }
  },
};
</script>
<style scoped lang="scss">
::v-deep .el-tag {
  margin-right: 10px;
}
.icon {
  cursor: pointer;
}
</style>
