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
  <div class="main-center">
    <div class="main-center-container">
      <indicator-list v-if="panelMode === 'list'" :currentData="currentData" />

      <indicator-panel v-else :currentData="currentData" />
    </div>
  </div>
</template>

<script>
import { computed, ref, watch } from 'vue';
import IndicatorPanel from './indicatorPanel';
import IndicatorList from './indicatorList';
import { useRoute } from 'vue-router';
export default {
  name: 'Indicator',
  props: {
    data: {
      type: Object,
    },
  },
  components: {
    IndicatorPanel,
    IndicatorList,
  },
  setup(props) {
    let route = useRoute();
    let panelMode = ref('list');
    let currentData = computed(() => props.data);
    watch(
      () => route.params.panel,
      (newValue) => {
        panelMode.value = newValue;
      },
      {
        immediate: true,
      }
    );
    return {
      panelMode,
      currentData,
    };
  },
};
</script>

<style scoped lang="scss">
.main-center {
  min-height: calc(100% - 243px);
  padding: 20px;
  background: #f9fbfc;
  &-container {
    background: #fff;
    border-radius: 4px;
  }
}
</style>
