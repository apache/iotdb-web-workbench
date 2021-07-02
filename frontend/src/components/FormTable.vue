<template>
  <el-form :model="formData" :rules="rules" :inline="inline" :label-position="labelPosition" :class="(inline ? 'demo-form-inline' : '', 'form_style')">
    <el-form-item v-for="item of formItem" :key="item.itemID" :label="$t(item.label)" :prop="item.itemID" :style="{ width: item.labelWidth }">
      <el-input
        v-if="item.type === 'INPUT'"
        v-model="formData[item.itemID]"
        :size="item.size"
        :style="{ width: item.width }"
        :placeholder="item.placeholder"
        @blur="getFormData"
        :suffix-icon="item.suffixIcon"
        :prefix-icon="item.prefixIcon"
      >
      </el-input>
      <el-input :size="item.size" v-if="item.type === 'TEXT'" v-model="formData[item.itemID]" class="input-inner" :suffix-icon="item.suffixIcon" :prefix-icon="item.prefixIcon" readonly> </el-input>
      <el-date-picker
        v-model="formData[item.itemID]"
        v-if="item.type === 'DATE'"
        :size="item.size"
        prefix-icon=""
        range-separator="~"
        type="datetimerange"
        :start-placeholder="item.startPlaceholder"
        :end-placeholder="item.endPlaceholder"
      >
      </el-date-picker>
    </el-form-item>
  </el-form>
</template>

<script>
import { ElForm, ElFormItem, ElInput, ElDatePicker } from 'element-plus';
import { reactive, toRefs } from 'vue';
export default {
  name: 'FormTable',
  props: {
    form: Array,
  },
  setup(props) {
    const formObj = reactive(props.form);
    let prop = {};
    const requiredArry = formObj.formItem.filter((item) => item.required);
    if (requiredArry.length > 0) {
      requiredArry.forEach((item) => {
        prop[item.itemID] = [
          {
            required: true,
            message: item.message,
            trigger: item.type === 'INPUT' ? 'blur' : 'change',
          },
        ];
      });
    }
    const rules = reactive(prop);
    function getFormData() {
      console.log(formObj.formData);
    }
    return {
      ...toRefs(formObj),
      rules,
      getFormData,
    };
  },
  components: {
    ElForm,
    ElFormItem,
    ElInput,
    ElDatePicker,
  },
};
</script>

<style lang="scss" scoped>
.demo-form-inline {
  display: flex;
}
</style>
<style lang="scss">
.form_style {
  .el-form-item__content {
    vertical-align: middle;
    line-height: 30px;
  }
}
</style>