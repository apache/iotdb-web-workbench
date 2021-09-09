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
  <div class="font_fmiy">
    <textarea ref="textarea" @input="change(value)"></textarea>
  </div>
</template>

<script>
import 'codemirror/theme/neo.css';
import 'codemirror/theme/neat.css';
import 'codemirror/mode/sql/sql.js';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/hint/show-hint';
// import 'codemirror/addon/hint/sql-hint';
import _CodeMirror from 'codemirror';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/cobalt.css';
import 'codemirror/mode/css/css.js';
import 'codemirror/mode/xml/xml.js';
import 'codemirror/addon/selection/active-line';
import 'codemirror/addon/selection/selection-pointer';
import 'codemirror/addon/edit/matchbrackets';
import handleShowHint from '../hooks/codemirror';
import keywords from '../hooks/keywords';
const CodeMirror = window.CodeMirror || _CodeMirror;
export default {
  name: 'Sqlserch',
  props: {
    codes: String,
    divwerHeight: Number,
  },
  data() {
    return {
      code: this.codes,
      mode: { name: 'text/x-mysql' },
      options: {
        mode: { name: 'text/x-mysql' },
        tabSize: 1,
        theme: 'neat',
        lineNumbers: true,
        line: true,
        lineWrapping: true, // 自动换行
        styleActiveLine: true, // 当前行背景高亮
        smartIndent: true,
        matchBrackets: true,
        hintOptions: {
          completeSingle: false,
          hint: handleShowHint,
        },
        indentUnit: 4, // 缩进单位为4
        extraKeys: {
          Ctrl: 'autocomplete',
          Tab: (cmInstance) => {
            let cursor = cmInstance.getCursor();
            let cursorLine = cmInstance.getLine(cursor.line);
            let end = cursor.ch;
            let start = end;
            let token = cmInstance.getTokenAt(cursor);
            console.log(cmInstance, cursor, cursorLine, start, end, token);
            console.log(cursorLine);
            cmInstance.setSelection({ line: cursor.line, ch: cursorLine.indexOf('<') }, { line: cursor.line, ch: cursorLine.indexOf('>') + 1 });
            // this.$emit('getCode', cursor.line, cursorLine);
          },
        },
        // styleActiveLine: true,
      },
    };
  },
  mounted() {
    this._initialize();
  },
  methods: {
    onCmCodeChange(content) {
      console.log(content);
      // this.coder.setValue(content);
      this.coder.replaceSelection(content);
      console.log(this.coder.getCursor());
    },
    codemrriorHeight(val) {
      this.coder.setSize('auto', `calc(100vh - ${143 + val}px)`);
    },
    setCode(value) {
      this.coder.setValue(value);
    },
    setEvent() {
      this.coder.on('change', () => {
        this.$emit('getCode', this.code);
        handleShowHint(this.coder);
        //编译器内容更改事件
        this.coder.showHint();
      });
    },
    _initialize() {
      // 初始化编辑器实例，传入需要被实例化的文本域对象和默认配置
      this.coder = CodeMirror.fromTextArea(this.$refs.textarea, this.options);
      // 编辑器赋值
      // this.coder.setSize("auto", `calc(100vh - 443px)`);
      // 支持双向绑定
      this.coder.on('change', (coder) => {
        this.code = coder.getValue();
      });
      keywords.forEach((words) => {
        CodeMirror.resolveMode('text/x-mysql').keywords[words.toLowerCase()] = true;
      });
      // 尝试从父容器获取语法类型
      if (this.language) {
        // 获取具体的语法类型对象
        let modeObj = this._getLanguage(this.language);

        // 判断父容器传入的语法是否被支持
        if (modeObj) {
          this.mode = modeObj.label;
        }
      }
    },
    // 获取当前语法类型
    _getLanguage(language) {
      // 在支持的语法类型列表中寻找传入的语法类型
      return this.modes.find((mode) => {
        // 所有的值都忽略大小写，方便比较
        let currentLanguage = language.toLowerCase();
        let currentLabel = mode.label.toLowerCase();
        let currentValue = mode.value.toLowerCase();
        // 由于真实值可能不规范，例如 java 的真实值是 x-java ，所以讲 value 和 label 同时和传入语法进行比较
        return currentLabel === currentLanguage || currentValue === currentLanguage;
      });
    },
  },
};
</script>
<style lang="scss">
.CodeMirror-linenumber {
  padding: 5px 3px 5px 0px !important;
}
pre.CodeMirror-line {
  padding: 5px 20px !important;
  text-align: left !important;
}
.CodeMirror {
  background: #f7f7f7 !important;
}
.CodeMirror-gutters {
  background-color: #efefef !important;
  width: 30px !important;
}
.font_fmiy {
  .CodeMirror-line {
    font-size: 11px !important;
    font-family: 'PingFang SC, Arial, sans-serif' !important;
  }
}
</style>
