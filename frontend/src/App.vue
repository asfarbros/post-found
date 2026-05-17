<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router'

const router = useRouter()

function logout() {
  // Clear any stored user/session data
  localStorage.removeItem('user')
  localStorage.removeItem('token')

  // Redirect to login page
  router.push('/login')
}
</script>

<template>
  <main>
    <RouterView /> 
  </main>
</template>

<style scoped>
main {
  padding: 20px;
}
</style>
