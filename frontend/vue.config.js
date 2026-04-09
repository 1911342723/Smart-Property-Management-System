const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  chainWebpack: (config) => {
    config.plugin('copy').tap((args) => {
      const options = args[0] || {}
      const patterns = options.patterns || []

      options.patterns = patterns.map((pattern) => {
        const globOptions = pattern.globOptions || {}
        const ignore = new Set(globOptions.ignore || [])

        ignore.add('**/index.html')
        ignore.add('index.html')

        return {
          ...pattern,
          globOptions: {
            ...globOptions,
            ignore: Array.from(ignore)
          }
        }
      })

      args[0] = options
      return args
    })
  },
  devServer: {
    port: 8080,
    host: '0.0.0.0', // 允许外部访问
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  },
  css: {
    loaderOptions: {
      sass: {
        additionalData: `@import "@/styles/variables.scss";`
      }
    }
  },
  configureWebpack: {
    resolve: {
      fallback: {
        "path": require.resolve("path-browserify")
      }
    }
  }
})
