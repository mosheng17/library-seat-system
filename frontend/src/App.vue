<template>
  <div class="app-shell">
    <header class="top-bar">
      <h1>图书馆自习室座位预约系统</h1>
      <p>课程实验工程骨架</p>
      <nav class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link to="/login">登录</router-link>
        <router-link to="/reservation">预约</router-link>
        <router-link to="/records">我的预约</router-link>
        <router-link to="/admin">管理</router-link>
      </nav>
    </header>
    <main class="page-content">
      <router-view />
    </main>
  </div>
</template>
