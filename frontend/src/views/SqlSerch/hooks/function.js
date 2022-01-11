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

const list = [
  {
    label: 'sqlserch.Aggregate',
    value: 'sqlserch.Aggregate',
    children: [
      { label: 'COUNT', value: 'sqlserch.count' },
      { label: 'AVG', value: 'sqlserch.avg' },
      { label: 'SUM', value: 'sqlserch.sum1' },
      { label: 'FIRST_VALUE', value: 'sqlserch.fristvalue' },
      { label: 'LAST_VALUE', value: 'sqlserch.lastvalue' },
      { label: 'MIN_VALUE', value: 'sqlserch.minvalue' },
      { label: 'MAX_VALUE', value: 'sqlserch.maxvalue' },
      { label: 'MIN_TIME', value: 'sqlserch.mintime' },
      { label: 'MAX_TIME', value: 'sqlserch.maxtime' },
    ],
  },
  {
    label: 'sqlserch.math',
    value: 'sqlserch.math',
    children: [
      { label: 'SIN', value: 'sqlserch.sin' },
      { label: 'COS', value: 'sqlserch.cos' },
      { label: 'TAN', value: 'sqlserch.tan' },
      { label: 'ASIN', value: 'sqlserch.asin' },
      { label: 'ACOS', value: 'sqlserch.acos' },
      { label: 'ATAN', value: 'sqlserch.atan' },
      { label: 'DEGREES', value: 'sqlserch.degress' },
      { label: 'RADIANS', value: 'sqlserch.randians' },
      { label: 'SIGN', value: 'sqlserch.sing' },
      { label: 'CEIL', value: 'sqlserch.ceil' },
      { label: 'FLOOR', value: 'sqlserch.floor' },
      { label: 'ROUND', value: 'sqlserch.round' },
      { label: 'EXP', value: 'sqlserch.exp' },
      { label: 'LN', value: 'sqlserch.ln' },
      { label: 'LOG10', value: 'sqlserch.log10' },
      { label: 'SQRT', value: 'sqlserch.sqrt' },
      { label: 'ABS', value: 'sqlserch.abs' },
    ],
  },
  {
    label: 'sqlserch.string',
    value: 'sqlserch.string',
    children: [
      { label: 'STRING_CONTAINS', value: 'sqlserch.string_cont' },
      { label: 'STRING_MATCHES', value: 'sqlserch.string_mat' },
    ],
  },
  {
    label: 'sqlserch.select',
    value: 'sqlserch.select',
    children: [
      { label: 'TOP_K', value: 'sqlserch.tok' },
      { label: 'BOTTOM_K', value: 'sqlserch.bottomk' },
    ],
  },
  {
    label: 'sqlserch.sum',
    value: 'sqlserch.sum',
    children: [
      { label: 'TIME_DIFFERENCE', value: 'sqlserch.time_d' },
      { label: 'DIFFERENCE', value: 'sqlserch.diff' },
      { label: 'NON_NEGATIVE_DIFFERENCE', value: 'sqlserch.non' },
      { label: 'DERIVATIVE', value: 'sqlserch.deriv' },
      { label: 'NON_NEGATIVE_DERIVATIVE', value: 'sqlserch.non_n' },
    ],
  },
  {
    label: 'sqlserch.date',
    value: 'sqlserch.date',
    children: [{ label: 'NOW', value: 'sqlserch.now' }],
  },
];
export default list;
