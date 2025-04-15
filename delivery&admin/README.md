# 外卖配送管理系统前端

这是一个基于 Vue 3 + TypeScript 开发的外卖配送管理系统前端项目，包含配送员和管理员两个角色的登录注册和管理功能。

## 功能特点

- 支持配送员和管理员两种角色的登录注册
- 配送员功能：订单接单、配送管理、个人信息管理
- 管理员功能：用户管理、订单管理、系统设置

## 技术栈

- Vue 3
- TypeScript
- Vue Router
- Pinia (状态管理)
- Axios (HTTP请求)

## 安装和运行

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

### 构建生产环境

```bash
npm run build
```

## 后端API配置

默认情况下，API请求会发送到 `/api` 路径。如果需要连接到其他后端API地址，可以在 `.env` 文件中配置 `VITE_API_BASE_URL` 变量。

例如:
```
# .env.development
VITE_API_BASE_URL=http://localhost:3000/api
```

## 前后端对接指南

### API服务层

我们在 `src/services` 目录下实现了一套完整的API服务层，用于与后端进行通信。这些服务封装了所有与后端的交互细节，提供了一个简洁的接口供组件使用。

主要文件包括:

- `api.ts` - 基础API配置，包含axios实例和拦截器
- `authService.ts` - 认证相关API
- `deliveryService.ts` - 配送相关API
- `adminService.ts` - 管理员相关API
- `index.ts` - 统一导出所有服务

详细的API服务说明请查看 [src/services/README.md](src/services/README.md)。

### 后端对接步骤

1. **配置API基础URL**：
   在 `.env.development` 文件中设置 `VITE_API_BASE_URL` 变量指向你的后端API地址。

2. **实现对应的API端点**：
   根据 `src/services` 中定义的各个服务所需的API端点，在后端实现对应的接口。

3. **数据格式**：
   参考服务文件中定义的TypeScript接口，确保后端返回的数据格式与前端期望的格式一致。

4. **认证机制**：
   系统使用JWT认证，后端需要实现相应的认证机制，生成和验证JWT token。

### 使用代理进行开发

在开发环境中，可以使用Vite的代理功能转发API请求到实际的后端服务器：

```javascript
// vite.config.ts
export default defineConfig({
  // ...
  server: {
    proxy: {
      '/api': {
        target: 'http://后端服务器地址',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
});
```

## 项目结构

```
src/
├── assets/       # 静态资源
├── components/   # 通用组件
├── router/       # 路由配置
├── services/     # API服务
│   ├── api.ts              # 基础API配置
│   ├── authService.ts      # 认证服务
│   ├── deliveryService.ts  # 配送服务
│   ├── adminService.ts     # 管理员服务
│   ├── index.ts            # 服务统一导出
│   └── README.md           # API服务说明
├── store/        # Pinia 状态管理
│   ├── auth.ts             # 认证状态
│   └── ...
├── views/        # 页面组件
│   ├── auth/     # 认证相关页面
│   ├── delivery/ # 配送员页面
│   └── admin/    # 管理员页面
├── App.vue       # 根组件
└── main.ts       # 应用入口
```

## 开发团队

- 前端开发: [您的名字]
- 后端开发: [后端开发者名字]

## 许可证

[MIT](LICENSE)

# Vue 3 + TypeScript + Vite

This template should help get you started developing with Vue 3 and TypeScript in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about the recommended Project Setup and IDE Support in the [Vue Docs TypeScript Guide](https://vuejs.org/guide/typescript/overview.html#project-setup).
