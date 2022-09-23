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

const path = require('path');
function resolve(dir) {
  return path.join(__dirname, dir);
}

const Version = new Date().getTime();

module.exports = {
  chainWebpack: (config) => {
    config.resolve.alias.set('@', resolve('./src')).set('components', resolve('./src/components'));
  },
  css: {
    loaderOptions: {
      sass: {
        prependData: `@use "@/styles/variables.scss" as *;`,
        sassOptions: {
          outputStyle: 'expanded',
        },
      },
    },
    extract: {
      filename: `css/[name].${Version}.css`,
      chunkFilename: `css/chunk.[id].${Version}.css`,
    },
  },
  configureWebpack: {
    output: {
      filename: `js/[name].${Version}.js`,
      chunkFilename: `js/chunk.[id].${Version}.js`,
    },
  },
  productionSourceMap: false,
  devServer: {
    proxy: {
      '/.*': {
        target: 'http://192.168.1.84:9090',
        progress: false,
      },
    },
  },
};
