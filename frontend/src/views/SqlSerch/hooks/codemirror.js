import keywords from './keywords';
function handleShowHint(cmInstance) {
  let cursor = cmInstance.getCursor();
  let list = [...keywords];
  let token = cmInstance.getTokenAt(cursor);
  list = list.filter((item) => item.indexOf(token.string.toLocaleUpperCase()) === 0);
  return {
    list: list,
    from: { ch: token.start, line: cursor.line },
    to: { ch: token.end, line: cursor.line },
  };
}

export default handleShowHint;
