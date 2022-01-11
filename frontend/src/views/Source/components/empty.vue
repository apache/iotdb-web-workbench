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
  <div class="empty-page">
    <div class="img"></div>
    <p>
      {{ $t('rootPage.nodatasource') }}<el-button type="text" @click="newSource()">{{ $t('rootPage.newdatasource') }}</el-button>
    </p>
    <NewSource v-if="showDialog" :func="func" :serverId="null" :showDialog="showDialog" :types="types" @close="close()" @successFunc="successFunc(data)" />
  </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref } from 'vue';
import { ElButton } from 'element-plus';
import { useI18n } from 'vue-i18n';
// import axios from '@/util/axios.js';
// import { useStore } from 'vuex';
// import { useRoute } from 'vue-router';
import NewSource from './newSource.vue';

export default {
  name: 'Empty',
  props: ['func'],
  setup(props) {
    const { t } = useI18n();
    // const router = useRoute();
    let showDialog = ref(false);
    let types = ref(0);

    /**
     * new source
     */
    const newSource = () => {
      showDialog.value = true;
      types.value = 0;
    };
    /**
     * close or cancel new/edit souce
     */
    const close = () => {
      showDialog.value = false;
      types.value = 0;
    };
    /**
     * successful func
     */
    const successFunc = () => {
      showDialog.value = false;
      types.value = 0;
      props.func.removeTab(props.data.id);
    };
    onMounted(() => {});

    return {
      showDialog,
      types,
      newSource,
      successFunc,
      close,
      t,
    };
  },
  components: {
    ElButton,
    NewSource,
  },
};
</script>

<style scoped lang="scss">
.empty-page {
  margin-top: 20vh;
  .img {
    background: url(~@/assets/empty.png) no-repeat;
    background-size: contain;
    width: 180px;
    height: 136px;
    margin: 0 auto;
  }
}
</style>
