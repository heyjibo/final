import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

/**
 * Vite配置
 * @see https://vitejs.dev/config/
 */
export default defineConfig({
  plugins: [vue()],
  
  // 开发服务器配置
  server: {
    port: 3000,         // 开发服务器端口
    strictPort: false,  // 如果端口被占用是否尝试其他端口
    open: true,         // 启动后自动打开浏览器
    
    // API代理配置 - 用于开发环境
    // 这里配置了'/api'前缀的请求将被代理到后端服务器
    // 后端开发者可修改此配置连接到实际的后端服务
    proxy: {
      '/api': {
        // 将请求代理到的目标地址
        // 后端开发人员请将此地址修改为你的API服务地址
        target: 'http://localhost:8080',
        
        // 是否更改源头，实际请求中的host头会被设置成target
        changeOrigin: true,
        
        // 是否启用WebSocket代理
        ws: true,
        
        // 路径重写，可选
        // 如果后端API不需要'/api'前缀，可以启用此选项
        // rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  
  // 生产环境构建配置
  build: {
    // 构建输出目录
    outDir: 'dist',
    
    // 将CSS提取到单独的文件
    cssCodeSplit: true,
    
    // 源码映射，用于调试
    sourcemap: false,
    
    // 压缩选项
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,  // 移除console
        drop_debugger: true  // 移除debugger
      }
    }
  }
})
