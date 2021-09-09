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
  <div class="home">
    <HelloWorld msg="Welcome to Your Vue.js App" />
    <iot-button>默认按钮1</iot-button>
    <p>{{ $t('message.hello') }}</p>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from '@/components/HelloWorld.vue';
import IotButton from '@/lib/Button.vue';
import { onMounted } from 'vue';
import axios from '@/util/axios.js';

export default {
  name: 'Home',
  setup() {
    onMounted(() => {
      axios.get('/data/cityinfo/101010100.html', {}).then((res) => {
        console.log(res, 'kk');
      });
    });
  },
  components: {
    HelloWorld,
    IotButton,
  },
};
</script>

<style scoped lang="scss">
.home {
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
