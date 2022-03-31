<!--
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
-->

<template>
  <div class="control-page-wraper">
    <div class="left-list">dsds（另写一个组件引入到该地方）</div>
    <div class="right-content">
      <el-tabs v-model="activeTab" @tab-click="handleClick">
        <el-tab-pane label="指标" name="indicator"></el-tab-pane>
        <el-tab-pane label="查询" name="query"> </el-tab-pane>
      </el-tabs>
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref } from 'vue';
import { ElTabs } from 'element-plus';
import { useRouter } from 'vue-router';

export default {
  name: 'About',
  setup() {
    const router = useRouter();
    let activeTab = ref('indicator');
    onMounted(() => {});
    const handleClick = (tabs) => {
      activeTab.value = tabs.paneName;
      if (tabs.paneName == 'indicator') {
        router.push({ name: 'Indicator' });
      } else if (tabs.paneName == 'query') {
        router.push({ name: 'Query' });
      }
    };

    return {
      activeTab,
      handleClick,
    };
  },
  components: {
    ElTabs,
  },
};
</script>

<style scoped lang="scss">
.control-page-wraper {
  display: flex;
  .left-list {
    width: 300px;
  }
  .right-content {
    flex: 1;
  }
}
</style>
