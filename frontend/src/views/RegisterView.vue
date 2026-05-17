<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const name=ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)

async function registerUser() {
  if (!email.value || !password.value || !confirmPassword.value) {
    error.value = 'Please fill in all required fields.'
    return
  }

  if (password.value.length < 6) {
    error.value = 'Password must be at least 6 characters long.'
    return
  }

  if (password.value !== confirmPassword.value) {
    error.value = 'Passwords do not match.'
    return
  }

  loading.value = true
  error.value = ''
  success.value = ''

  try {
    const response = await axios.post('http://localhost:8080/api/auth/register', {
      name:name.value,
      email: email.value,
      password: password.value
    })

    if (response.status === 200 || response.status === 201) {
      success.value = 'Registration successful! Redirecting to login...'
      setTimeout(() => router.push({ name: 'login' }), 2000)
    } else {
      error.value = 'Something went wrong. Please try again.'
    }
  } catch (err) {
    console.error(err)
    error.value = 'Email already exists or server error.'
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
      <div class="hidden md:flex md:flex-1 md:flex-col md:justify-center md:items-center bg-linear-to-tr from-[#5a6ff1] to-[#3f2b96] text-white text-center py-10 px-5">
        <img
          src="/images/lost-found-bg.png"
          alt="Lost and Found"
          class="w-[180px] mb-5 drop-shadow-[0_6px_8px_rgba(0,0,0,0.2)]"
        />
        <h1 class="text-[2rem] font-bold mb-2.5">Join the Community 💫</h1>
        <p class="text-base opacity-90">Register now to post or claim lost items easily.</p>
      </div>

      <div class="flex-1 py-10 px-[25px] md:py-[50px] md:px-10 flex flex-col justify-center">
        <h2 class="text-[#3f2b96] mb-[25px] text-center text-3xl font-bold">Create Account</h2>
        <form @submit.prevent="registerUser" class="flex flex-col gap-5">
          <div class="flex flex-col">
            <label for="name" class="font-semibold text-[#555] mb-1.5">Full Name</label>
            <input
              id="name"
              type="text"
              v-model="name"
              placeholder="Enter your full name"
              required
              class="w-full p-3 rounded-lg border border-[#ccc] text-base outline-none transition duration-200 ease-in-out focus:border-[#3f2b96] focus:ring-[3px] focus:ring-[rgba(63,43,150,0.1)]"
            />
          </div>

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
              placeholder="Enter a strong password"
              required
              class="w-full p-3 rounded-lg border border-[#ccc] text-base outline-none transition duration-200 ease-in-out focus:border-[#3f2b96] focus:ring-[3px] focus:ring-[rgba(63,43,150,0.1)]"
            />
          </div>

          <div class="flex flex-col">
            <label for="confirmPassword" class="font-semibold text-[#555] mb-1.5">Confirm Password</label>
            <input
              id="confirmPassword"
              type="password"
              v-model="confirmPassword"
              placeholder="Re-enter your password"
              required
              class="w-full p-3 rounded-lg border border-[#ccc] text-base outline-none transition duration-200 ease-in-out focus:border-[#3f2b96] focus:ring-[3px] focus:ring-[rgba(63,43,150,0.1)]"
            />
          </div>

          <div v-if="error" class="text-[#e74c3c] text-center bg-[#fdecea] border border-[#e74c3c] rounded-md p-2.5 text-[0.9rem]">
            {{ error }}
          </div>
          <div v-if="success" class="text-[#27ae60] text-center bg-[#eafaf1] border border-[#27ae60] rounded-md p-2.5 text-[0.9rem]">
            {{ success }}
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full p-3 rounded-lg border-none bg-[#3f2b96] text-white font-bold text-base cursor-pointer transition duration-300 ease-in-out hover:bg-[#34287d] disabled:opacity-70 disabled:cursor-not-allowed"
          >
            {{ loading ? 'Creating Account...' : 'Register' }}
          </button>

          <p class="text-center mt-2.5 text-[#333]">
            Already have an account?
            <router-link to="/login" class="text-[#f39c12] no-underline font-bold hover:underline">
              Login
            </router-link>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>