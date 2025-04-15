# API 服务层

这个目录包含了所有与后端API交互的服务。服务层的目的是封装所有API请求的细节，提供一个干净的接口给组件使用。

## 架构

服务层包含以下文件：

- **api.ts** - 基础API配置，包含axios实例和拦截器
- **authService.ts** - 认证相关API，包括登录、注册等
- **deliveryService.ts** - 配送相关API，包括订单获取、更新状态等
- **adminService.ts** - 管理员相关API，包括用户管理、统计信息等
- **index.ts** - 统一导出所有服务
- **mockService.ts** - 模拟API服务，用于前端开发测试

## 后端对接指南

### 基础URL配置

在`api.ts`中，我们配置了基础URL路径：

```typescript
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  // ...
});
```

您可以在`.env`文件中设置`VITE_API_BASE_URL`环境变量来配置API的基础URL。如果不设置，默认使用`/api`作为API前缀。

### API路径

所有API路径遵循以下规范：

1. 认证相关：
   - `/auth/delivery/login` - 配送人员登录
   - `/auth/admin/login` - 管理员登录
   - `/auth/delivery/register` - 配送人员注册
   - `/auth/admin/register` - 管理员注册
   - `/auth/user` - 获取当前用户信息
   - `/auth/logout` - 登出

2. 配送相关：
   - `/delivery/orders/pending` - 获取待配送订单
   - `/delivery/orders/my` - 获取我的配送订单
   - `/delivery/orders/:id` - 获取订单详情
   - `/delivery/orders/:id/accept` - 接受订单
   - `/delivery/orders/:id/pickup` - 标记已取餐
   - `/delivery/orders/:id/deliver` - 标记已送达
   - `/delivery/stats` - 获取配送统计
   - `/delivery/profile` - 更新个人信息

3. 管理员相关：
   - `/admin/orders` - 获取所有订单
   - `/admin/orders/:id` - 获取订单详情
   - `/admin/orders/:id/status` - 更新订单状态
   - `/admin/users/delivery` - 获取配送人员列表
   - `/admin/users/admin` - 获取管理员列表
   - `/admin/users/:id/status` - 更新用户状态
   - `/admin/statistics/orders` - 获取订单统计
   - `/admin/statistics/users` - 获取用户统计
   - `/admin/statistics/revenue` - 获取收入统计
   - `/admin/export/orders` - 导出订单数据

### 认证机制

API使用JWT进行认证，token会被存储在`localStorage`中。API拦截器会自动添加认证头：

```typescript
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});
```

### 错误处理

API响应拦截器会处理常见错误：

- 401 - 未授权，会自动跳转到登录页面
- 403 - 禁止访问
- 404 - 资源未找到
- 500 - 服务器错误

### Mock服务

为了方便前端开发和测试，我们提供了模拟API服务。这些服务可以在没有后端API的情况下使用，模拟真实的API调用行为。

Mock服务位于`mockService.ts`文件中，包括：

- `mockAuthService` - 认证相关模拟服务
- `mockDeliveryService` - 配送相关模拟服务

#### 如何使用Mock服务

在没有实际后端API的情况下，可以通过以下方式使用Mock服务：

```typescript
// 在组件中
import { mockAuthService as authService } from '@/services/mockService';

// 使用方式与真实API服务相同
const login = async () => {
  try {
    const result = await authService.login({
      username: 'delivery1',
      password: '123456'
    }, 'delivery');
    
    console.log('登录成功', result.user);
  } catch (error) {
    console.error('登录失败', error);
  }
};
```

> 注意：Mock服务提供了固定的测试账号：
> - 配送员: username=`delivery1`, password=`123456`
## 数据模型

所有数据模型都在各自的服务文件中定义，例如：

- User - 用户信息
- Order - 订单信息
- OrderStatistics - 订单统计
- 等等

您可以查看各个服务文件中的接口定义，了解前端所需的数据结构。 