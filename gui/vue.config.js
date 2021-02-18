const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer')
const appConfig = require('./src/app.config')

module.exports = {
  configureWebpack: {
    // We provide the app's title in Webpack's name field, so that
    // it can be accessed in index.html to inject the correct title.
    name: appConfig.title,
    // Set up all the aliases we use in our app.
    resolve: {
      alias: require('./aliases.config').webpack,
    },
    output: {
      filename: '[name]-[hash:7].js',
      chunkFilename: '[name]-[hash:7].js',
    },
    plugins: [
      // Optionally produce a bundle analysis
      new BundleAnalyzerPlugin({
        analyzerMode: process.env.ANALYZE ? 'static' : 'disabled',
        openAnalyzer: process.env.CI !== 'true',
      }),
    ],
  },
  baseUrl: '.',
  css: {
    // Enable CSS source maps.
    sourceMap: true,
  },

  chainWebpack: config => {
    if (config.plugins.has('extract-css')) {
      const extractCSSPlugin = config.plugin('extract-css')
      extractCSSPlugin &&
        extractCSSPlugin.tap(() => [
          {
            filename: '[name]-[hash:7].css',
            chunkFilename: '[name]-[hash:7].css',
          },
        ])
    }
  },

  // Configure Webpack's dev server.
  // https://github.com/vuejs/vue-cli/blob/dev/docs/cli-service.md
  devServer: {
    ...(process.env.API_BASE_URL
      ? // Proxy API endpoints to the production base URL.
        { proxy: { '/api': { target: process.env.API_BASE_URL } } }
      : // Proxy API endpoints a local mock API.
        { before: require('./tests/mock-api') }),
  },
}
