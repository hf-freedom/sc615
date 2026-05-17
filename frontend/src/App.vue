<template>
  <div id="app">
    <el-container style="height: 100vh; border: 1px solid #eee">
      <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
        <el-menu router active-text-color="#409EFF" default-active="/">
          <el-menu-item index="/">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/books">
            <el-icon><Reading /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/borrow-records">
            <el-icon><Document /></el-icon>
            <span>借阅记录</span>
          </el-menu-item>
          <el-menu-item index="/statistics">
            <el-icon><DataAnalysis /></el-icon>
            <span>统计报表</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="text-align: right; font-size: 14px; background: #fff; border-bottom: 1px solid #eee">
          <h2 style="float: left; margin: 0; line-height: 60px">图书馆借阅逾期系统</h2>
        </el-header>
        <el-main style="background: #f5f7fa">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { House, Reading, User, Document, DataAnalysis } from '@element-plus/icons-vue'
</script>

<style>
* {
  margin: 0;
  padding: 0;
}
</style>
