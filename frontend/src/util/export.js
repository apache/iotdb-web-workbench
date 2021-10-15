export function handleExport(data, name) {
  const blob = new Blob([data]);
  const downloadElement = document.createElement('a');
  const href = window.URL.createObjectURL(blob); // 创建下载的链接
  downloadElement.href = href;
  downloadElement.download = name; // 下载后文件名
  document.body.appendChild(downloadElement);
  downloadElement.click(); // 点击下载
  document.body.removeChild(downloadElement); // 下载完成移除元素
  window.URL.revokeObjectURL(href); // 释放掉blob对象
}
export function loopDirectory(root) {
  const list = [root];
  const loop = (node) => {
    node.children = node.children || [];

    node.children.forEach((d) => {
      d.level = node.level + 1;
      d.path = [
        ...node.path,
        {
          name: d,
        },
      ];
      list.push(d);
      loop(d);
    });
  };
  loop(root);
  return list;
}