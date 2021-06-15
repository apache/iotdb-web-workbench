const path = require("path");
function resolve(dir) {
  return path.join(__dirname, dir);
}

module.exports = {
  chainWebpack: (config) => {
    config.resolve.alias
      .set("@", resolve("./src"))
      .set("components", resolve("./src/components"));
  },
  devServer: {
    proxy: {
      "/data": {
        target: "http://www.weather.com.cn",
        changeOrigin: true,
      },
    },
  },
};
