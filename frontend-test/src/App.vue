<template>
  <div id="app">
    <nav>
      <router-link to="/customer">Customer Management</router-link> | 
      <router-link to="/deliver">Deliver Management</router-link>
    </nav>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App'
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

nav {
  margin-bottom: 30px;
}

nav a {
  padding: 10px 20px;
  text-decoration: none;
  color: #2c3e50;
  border-radius: 4px;
  transition: background-color 0.3s;
}

nav a.router-link-exact-active {
  background-color: #409EFF;
  color: white;
}
</style>