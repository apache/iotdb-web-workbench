/* * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements. See the NOTICE file * distributed with this work for additional information * regarding
copyright ownership. The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License. You may obtain a
copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, * software distributed under the License is distributed on an * "AS
IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY * KIND, either express or implied. See the License for the * specific language governing permissions and limitations * under the License. */

<template>
  <el-form ref="formtable" :model="formData" :rules="rules" :inline="inline" :label-position="labelPosition" :class="{ 'demo-form-inline': inline, form_style: true, form_label: label }">
    <el-form-item v-for="item of formItem" :key="item.itemID" :label="$t(item.label)" :prop="item.itemID" :style="{ width: item.labelWidth }">
      <span v-if="label" class="icon_span">
        <i class="el-icon-question" />
      </span>
      <el-input
        v-if="item.type === 'INPUT'"
        v-model="formData[item.itemID]"
        :size="item.size"
        :style="{ width: item.width }"
        :placeholder="$t(item.placeholder)"
        :disabled="item.disabled"
        @blur="getFormData"
        @keyup.enter="getFormData"
        :suffix-icon="item.suffixIcon"
        :prefix-icon="item.prefixIcon"
      >
        <template #prepend v-if="item.inputHeader">{{ formData[item.inputHeaderText] }}</template>
      </el-input>
      <el-select v-if="item.type === 'SELECT'" v-model="formData[item.itemID]" :style="{ width: item.width }" :size="item.size" placeholder="请选择">
        <el-option v-for="item in item.options" :key="item.value" :label="item.label" :value="item.value"> </el-option>
      </el-select>
      <el-input :size="item.size" v-if="item.type === 'TEXT'" v-model="formData[item.itemID]" class="input-inner" :suffix-icon="item.suffixIcon" :prefix-icon="item.prefixIcon" readonly> </el-input>
      <el-date-picker
        v-model="formData[item.itemID]"
        v-if="item.type === 'DATE'"
        :size="item.size"
        :style="{ width: item.width }"
        prefix-icon=""
        range-separator="~"
        type="datetimerange"
        :start-placeholder="item.startPlaceholder"
        :end-placeholder="item.endPlaceholder"
        @blur="item.Event"
      >
      </el-date-picker>
      <el-date-picker v-model="formData[item.itemID]" v-if="item.type === 'DATETIME'" type="datetime" :style="{ width: item.width }" :placeholder="item.placeholder"> </el-date-picker>
      <el-input v-model="formData[item.itemID]" v-if="item.type === 'INPUTNUM'" :style="{ width: item.width }" controls-position="right">
        <template #append>
          <span v-if="item.unit" style="padding: 0 10px">{{ item.unit }}</span>
          <div class="icon_but">
            <div
              style="top: -1px"
              @click.stop="
                () => {
                  formData[item.itemID]++;
                }
              "
            >
              <span style="top: 0px">
                <i class="el-icon-caret-top" />
              </span>
            </div>
            <div
              class="down"
              style="bottom: -2px"
              @click.stop="
                () => {
                  formData[item.itemID]--;
                }
              "
            >
              <span style="bottom: 0px">
                <i class="el-icon-caret-bottom" />
              </span>
            </div>
          </div>
        </template>
      </el-input>
    </el-form-item>
    <input id="hiddenText" type="text" style="display: none" />
  </el-form>
</template>

<script>
import { ElForm, ElFormItem, ElInput, ElDatePicker, ElSelect, ElOption } from 'element-plus';
import { reactive, toRefs, ref } from 'vue';
// import { useI18n } from 'vue-i18n';
export default {
  name: 'FormTable',
  props: {
    form: Array,
    labelIcon: Boolean,
  },
  setup(props, { emit }) {
    // const { t } = useI18n();
    const formObj = reactive(props.form);
    const label = reactive(props.labelIcon);
    const formtable = ref(null);
    let prop = {};
    const requiredArry = formObj.formItem.filter((item) => item.required);
    if (requiredArry.length > 0) {
      requiredArry.forEach((item) => {
        prop[item.itemID] = item.rules;
      });
    }
    const rules = reactive(prop);
    function getFormData() {
      emit('serchFormData');
    }
    function checkData(obj) {
      formtable.value.validate((valid) => {
        if (valid) {
          alert('成功');
          obj.flag = false;
        } else {
          obj.flag = true;
        }
      });
    }
    return {
      ...toRefs(formObj),
      label,
      formtable,
      rules,
      checkData,
      getFormData,
    };
  },
  components: {
    ElForm,
    ElFormItem,
    ElInput,
    ElDatePicker,
    ElSelect,
    ElOption,
  },
};
</script>

<style lang="scss" scoped>
.demo-form-inline {
  display: flex;
}
</style>
<style lang="scss">
.form_label {
  .el-form-item__label {
    padding: 0px 12px;
  }
  .icon_span {
    position: absolute;
    top: 0px;
    left: 0px;
  }
}
.form_style {
  .el-form-item__content {
    vertical-align: middle;
    line-height: 30px;
  }
  .el-input__icon {
    line-height: 30px;
  }
  .el-input-group__append {
    line-height: 23px;
    background: #fff;
    padding: 0 0px;
    overflow: hidden;
  }
  .el-input-group__prepend {
    line-height: 23px;
  }
  .icon_but {
    flex-direction: column;
    display: inline-flex;
    width: 30px;
    justify-content: space-evenly;
    div {
      cursor: pointer;
      width: 30px;
      height: 13px;
      text-align: center;
      border: 1px solid #e7eaf2;
      position: absolute;
      span {
        right: 7px;
        position: absolute;
        line-height: 0px;
      }
    }
  }
}
.input-inner {
  .el-input__inner {
    border: none !important;
  }
}
</style>
