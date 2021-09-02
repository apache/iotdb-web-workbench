/*
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
 */

import { ref } from 'vue';

function useLangSwitch(domRef, dividerWidth) {
  let startX = ref(0);
  let endX = ref(0);
  let initWidth = dividerWidth.value;
  domRef.value.onmousedown = (e) => {
    document.documentElement.classList.add('move-resize');
    startX.value = e.clientX;
    endX.value = e.clientX;
    document.onmousemove = function (e) {
      endX.value = e.clientX;
      let val = initWidth + (endX.value - startX.value);
      if (val < 200) {
        return;
      }
      dividerWidth.value = val;
    };
    document.onmouseup = function () {
      document.documentElement.classList.remove('move-resize');
      initWidth = dividerWidth.value;
      document.onmousemove = null;
      document.onmouseup = null;
    };
  };
}

export default useLangSwitch;
