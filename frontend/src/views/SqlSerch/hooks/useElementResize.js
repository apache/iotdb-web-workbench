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

function useLangSwitch(domRef, divwerHeight) {
  let startY = ref(0);
  let endY = ref(0);
  let initWidth = divwerHeight.value;
  domRef.value.onmousedown = (e) => {
    document.documentElement.style.cursor = 'n-resize';
    startY.value = e.clientY;
    endY.value = e.clientY;
    document.onmousemove = function (e) {
      if (divwerHeight.value > 30) {
        endY.value = e.clientY;
        divwerHeight.value = initWidth + (startY.value - endY.value);
      } else {
        divwerHeight.value = 0;
      }
    };
    document.onmouseup = function () {
      document.documentElement.style.cursor = 'auto';
      initWidth = divwerHeight.value;
      document.onmousemove = null;
      document.onmouseup = null;
    };
  };
}

export default useLangSwitch;
