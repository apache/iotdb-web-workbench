import keywords from './keywords';
import selectList from './selectList';
function handleShowHint(cmInstance) {
  let arr = Object.keys(selectList);
  let cursor = cmInstance.getCursor();
  let list = [...keywords];
  let token = cmInstance.getTokenAt(cursor);
  list = list.filter((item) => item.indexOf(token.string.toLocaleUpperCase()) === 0);
  if (arr.includes(token.string.toLocaleUpperCase())) {
    list = selectList[token.string.toLocaleUpperCase()];
  }
  return {
    list: list,
    from: { ch: token.start, line: cursor.line },
    to: { ch: token.end, line: cursor.line },
  };
}

export default handleShowHint;
