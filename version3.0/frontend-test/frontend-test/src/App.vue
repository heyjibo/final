<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin: 20px;
}

:root {
  /* 优化后的清晰色板 */
  --primary-color: #253858;       /* 主色：深邃海军蓝 */
  --primary-light: #3a5078;      /* 主色浅版（过渡用） */
  --secondary-color: #f39c12;    /* 辅助色：鲜明橙黄 */
  --accent-color: #2980b9;       /* 强调色：柔和天空蓝 */
  --text-primary: #2d3436;       /* 主文字：深灰黑 */
  --text-secondary: #636e72;     /* 次文字：中灰 */
  --text-inverse: #ffffff;       /* 反色文字（深色背景用） */
  --gradient-bg: linear-gradient(135deg, var(--primary-color) 20%, var(--primary-light) 100%);
  --box-shadow: 0 8px 32px rgba(37, 56, 88, 0.12);
  --card-radius: 16px;
  --transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1); /* 保留通用过渡变量（其他模块可能需要） */
}

/* 基础重置 */
* { box-sizing: border-box; margin: 0; padding: 0; }
html { font-size: 16px; scroll-behavior: smooth; }
body {
  font-family: 'Poppins', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  line-height: 1.6;
  color: var(--text-primary);
  background: #f8f9fa;
}

/* 统一卡片样式 */
.el-card {
  border-radius: var(--card-radius) !important;
  box-shadow: var(--box-shadow) !important;
  transition: var(--transition) !important; /* 卡片自身的过渡保留（非导航栏） */
  border: 1px solid #e9ecef !important;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px rgba(37, 56, 88, 0.18) !important;
  }
}

/* 按钮统一样式 */
.el-button {
  transition: var(--transition) !important; /* 按钮过渡保留（非导航栏） */
  border-radius: 8px !important;
  font-weight: 600 !important;
  letter-spacing: 0.5px;

  &--primary {
    background: var(--gradient-bg) !important;
    border: none !important;
    color: var(--text-inverse) !important;

    &:hover {
      opacity: 0.95;
      transform: translateY(-2px);
      background: linear-gradient(135deg, var(--primary-light) 20%, var(--primary-color) 100%) !important;
    }
  }

  &--secondary {
    background: var(--secondary-color) !important;
    border: none !important;
    color: var(--text-inverse) !important;
  }
}

/* 表格样式优化 */
.el-table {
  &__header {
    th {
      background: var(--primary-color) !important;
      color: var(--text-inverse) !important;
      font-weight: 600 !important;
    }
  }

  &__row {
    &:hover td {
      background: #f1f3f5 !important;
    }
  }
}
/* 响应式布局容器 */
.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 1.5rem;
}

/* 华丽标题样式 */
.section-title {
  position: relative;
  padding-left: 1.5rem;
  margin: 2rem 0;
  font-size: 1.75rem;
  color: var(--primary-color);

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    height: 80%;
    width: 4px;
    background: var(--secondary-color);
  }
}

/* 响应式媒体查询 */
@media (max-width: 1200px) {
  html { font-size: 15px; }
  .container { padding: 0 1rem; }
}

@media (max-width: 768px) {
  html { font-size: 14px; }
  .el-table {
    overflow-x: auto;
    display: block;
    &__body-wrapper { overflow-x: auto; }
  }
  .statistics { flex-direction: column; }
}

@media (max-width: 480px) {
  .el-card { margin: 1rem 0; }
  .el-form-item__label { width: 100% !important; text-align: left !important; }
  .el-select { width: 100% !important; }
}

/* 动画效果（仅保留非导航栏动画） */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.fade-in { animation: fadeIn 0.6s ease-out; }

/* Google字体引入 */
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap');
</style>