const path = require('path');
function resolve(dir) {
  return path.join(__dirname, dir);
}

module.exports = {
  chainWebpack: (config) => {
    config.resolve.alias.set('@', resolve('./src')).set('components', resolve('./src/components'));
  },
  css: {
    loaderOptions: {
      sass: {
        prependData: `@import "@/styles/variables.scss";`,
      },
    },
  },
  devServer: {
    proxy: {
      '/.*': {
        target: 'http://113.207.43.87:8080',
        changeOrigin: true,
      },
    },
  },
};
