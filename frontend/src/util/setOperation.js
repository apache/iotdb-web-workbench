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
function array_remove_repeat(a) {
  // 去重
  var r = [];
  for (var i = 0; i < a.length; i++) {
    var flag = true;
    var temp = a[i];
    for (var j = 0; j < r.length; j++) {
      if (temp === r[j]) {
        flag = false;
        break;
      }
    }
    if (flag) {
      r.push(temp);
    }
  }
  return r;
}
export default function array_difference(a, b) {
  // 差集 a - b
  //clone = a
  var clone = a.slice(0);
  for (var i = 0; i < b.length; i++) {
    var temp = b[i];
    for (var j = 0; j < clone.length; j++) {
      if (temp === clone[j]) {
        //remove clone[j]
        clone.splice(j, 1);
      }
    }
  }
  return array_remove_repeat(clone);
}
