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
        lineWrapping: true, // Auto wrap
        styleActiveLine: true, // Highlight current line background
        smartIndent: true,
        matchBrackets: true,
        hintOptions: {
          completeSingle: false,
          hint: handleShowHint,
        },
        indentUnit: 4, // Indent in 4
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
    getSelecValue() {
      return this.coder.getSelection();
    },
    setEvent() {
      this.coder.on('change', () => {
        this.$emit('getCode', this.code);
        handleShowHint(this.coder);
        //Compiler content change event
        this.coder.showHint();
      });
    },
    _initialize() {
      // Initialize the editor instance, and pass in the text field object to be instantiated and the default configuration
      this.coder = CodeMirror.fromTextArea(this.$refs.textarea, this.options);
      // Editor assignment
      // this.coder.setSize("auto", `calc(100vh - 443px)`);
      // Support bidirectional binding
      this.coder.on('change', (coder) => {
        this.code = coder.getValue();
      });
      keywords.forEach((words) => {
        CodeMirror.resolveMode('text/x-mysql').keywords[words.toLowerCase()] = true;
      });
      // Trying to get syntax type from parent container
      if (this.language) {
        // Gets the specific syntax type object
        let modeObj = this._getLanguage(this.language);

        // Determine whether the syntax passed in by the parent container is supported
        if (modeObj) {
          this.mode = modeObj.label;
        }
      }
    },
    // Gets the current syntax type
    _getLanguage(language) {
      // Look for the incoming syntax type in the list of supported syntax types
      return this.modes.find((mode) => {
        // All values ignore case for comparison
        let currentLanguage = language.toLowerCase();
        let currentLabel = mode.label.toLowerCase();
        let currentValue = mode.value.toLowerCase();
        // Because the real value may not be standardized, for example, the real value of Java is x-java, so value and label are compared with the incoming syntax at the same time
        return currentLabel === currentLanguage || currentValue === currentLanguage;
      });
    },
  },
};
</script>
<style lang="scss">
.CodeMirror-linenumber {
  padding: 5px 3px 5px 0 !important;
}
pre.CodeMirror-line {
  padding: 5px 20px !important;
  text-align: left !important;
}
.CodeMirror {
  background: #f9fbfc !important;
  pre {
    &.CodeMirror-line {
      font-size: 11px !important;
      font-family: 'PingFang SC, Arial, sans-serif';
    }
  }
}
.CodeMirror-gutters {
  background-color: #f9fbfc !important;
  width: 30px !important;
  border-right: none !important;
}
.font_fmiy {
  .CodeMirror-scroll {
    background: #f9fbfc !important;
  }
}
</style>
