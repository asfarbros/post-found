<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function loginUser() {
  if (!email.value || !password.value) {
    error.value = 'Please enter both email and password.'
    return
  }

  loading.value = true
  error.value = ''

  try {
    const response = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/auth/login`, {
      email: email.value,
      password: password.value
    })

    if (response.data.token) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('userName', email.value)
      router.push({ name: 'dashboard' })
    } else {
      error.value = 'Invalid login response from server.'
    }
  } catch (err) {
    error.value = 'Invalid credentials or server error.'
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="h-screen bg-linear-to-tr from-[#a8c0ff] to-[#3f2b96] flex justify-center items-center p-5">
    <div
      class="flex flex-col md:flex-row flex-wrap bg-white rounded-2xl overflow-hidden shadow-[0_8px_25px_rgba(0,0,0,0.2)] w-full max-w-full md:w-[900px]"
    >
      <div class="hidden md:flex md:flex-1 bg-linear-to-tr from-[#5a6ff1] to-[#3f2b96] text-white text-center p-10 flex-col justify-center items-center">
        <img
          src="/images/lost-found-bg.png"
          alt="Lost and Found"
          class="w-[180px] mb-5 drop-shadow-[0_6px_8px_rgba(0,0,0,0.2)]"
        />
        <h1 class="text-[2rem] font-bold mb-2.5">Welcome Back 👋</h1>
        <p class="text-base opacity-90">Login to continue posting or claiming found items.</p>
      </div>

      <div class="flex-1 px-[25px] py-10 md:px-10 md:py-[50px] flex flex-col justify-center">
        <h2 class="text-[#3f2b96] mb-6 text-center text-3xl font-bold">Sign In</h2>
        <form @submit.prevent="loginUser" class="flex flex-col gap-5">
          <div class="flex flex-col">
            <label for="email" class="font-semibold text-[#555] mb-1.5">Email</label>
            <input
              id="email"
              type="email"
              v-model="email"
              placeholder="you@example.com"
              required
              class="w-full p-3 rounded-lg border border-[#ccc] text-base outline-none transition duration-200 ease-in-out focus:border-[#3f2b96] focus:ring-[3px] focus:ring-[rgba(63,43,150,0.1)]"
            />
          </div>

          <div class="flex flex-col">
            <label for="password" class="font-semibold text-[#555] mb-1.5">Password</label>
            <input
              id="password"
              type="password"
              v-model="password"
              placeholder="Enter your password"
              required
              class="w-full p-3 rounded-lg border border-[#ccc] text-base outline-none transition duration-200 ease-in-out focus:border-[#3f2b96] focus:ring-[3px] focus:ring-[rgba(63,43,150,0.1)]"
            />
          </div>

          <div
            v-if="error"
            class="text-[#e74c3c] text-center bg-[#fdecea] border border-[#e74c3c] rounded-md p-2.5 text-[0.9rem]"
          >
            {{ error }}
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full p-3 rounded-lg border-none bg-[#3f2b96] text-white font-bold text-base cursor-pointer transition duration-300 ease-in-out hover:bg-[#34287d] disabled:opacity-70 disabled:cursor-not-allowed"
          >
            {{ loading ? 'Logging in...' : 'Login' }}
          </button>

          <p class="text-center mt-2.5 text-[#333]">
            Don't have an account?
            <router-link to="/register" class="text-[#f39c12] no-underline font-bold hover:underline">
              Register
            </router-link>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>