<template>
  <div class="aui-wrapper aui-page__not-found">
    <div class="aui-content__wrapper">
      <div class="aui-content">
        <h2 class="title">404</h2>
        <p class="desc" v-html="$t('notFound.desc')"/>
        <div class="btn-bar">
          <el-button @click="$router.go(-1)">{{ $t('notFound.back') }}</el-button>
          <el-button type="primary" @click="$router.push({ name: 'home' })">{{ $t('notFound.home') }}</el-button>
        </div>
      </div>
    </div>
  </div>
</template>
