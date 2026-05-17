<script setup>
import { useRouter } from 'vue-router'
import LostAndFoundList from '../components/LostAndFoundList.vue'

const router = useRouter()
const userName = localStorage.getItem('userName') || 'Guest'

// Button functions
const goToPostFound = () => router.push({ name: 'post-found' })

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userName')
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-linear-to-tr from-[#dfe9f3] to-[#ffffff] text-[#333] py-10 px-5 text-center">
    
    <header class="mb-[30px]">
      <h1 class="text-3xl font-bold mb-2">Lost & Found Dashboard</h1>
      <p class="text-[#555] mb-[5px]">See all posted and claimed items below.</p>
      <p class="text-[0.95rem] text-[#666] mb-5">👤 Logged in as <strong>{{ userName }}</strong></p>
      
      <div class="flex justify-center items-center gap-4">
        <button
          class="py-3 px-7 border-none rounded-lg font-bold cursor-pointer text-base transition duration-200 text-white bg-[#27ae60] hover:bg-[#219150]"
          @click="goToPostFound"
        >
          Post Found Item
        </button>
        <button
          class="py-3 px-7 border rounded-lg font-bold cursor-pointer text-base transition duration-200 text-[#e74c3c] border-[#e74c3c] bg-white hover:bg-[#fdecea]"
          @click="handleLogout"
        >
          Logout
        </button>
      </div>
    </header>

    <section class="mt-5">
      <LostAndFoundList />
    </section>
  </div>
</template>