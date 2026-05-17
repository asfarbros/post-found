<script setup>
import { useRouter } from 'vue-router';
import { ref } from 'vue'; // Import ref

const router = useRouter();

// ✅ NEW: State to track hover
const isHoveringImage = ref(false); 
</script>

<template>
  <div class="flex h-screen flex-col md:flex-row">
    
    <div 
      class="relative flex h-1/2 w-full flex-col items-center justify-center p-10 
             md:h-screen md:w-1/2 overflow-hidden
             bg-linear-to-br from-[#a8c0ff] to-[#3f2b96]"
    >
      
      <div class="absolute inset-0 opacity-10" style="background-image: url('data:image/svg+xml,%3Csvg width=\'6\' height=\'6\' viewBox=\'0 0 6 6\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cg fill=\'%23ffffff\' fill-opacity=\'0.2\' fill-rule=\'evenodd\'%3E%3Cpath d=\'M5 0h1L0 6V5zm1 5v1H5z\'/%3E%3C/g%3E%3C/svg%3E');"></div>
      
      <div class="relative z-10 text-center text-white">
        <h1 class="mb-4 text-6xl font-extrabold leading-tight tracking-tight drop-shadow-lg animate-fade-in-down">
          Found <span class="text-[#ffe066]">&</span> Lost
        </h1>
        <p class="mb-6 text-xl text-blue-100 opacity-90 drop-shadow animate-fade-in-up">
          Connecting you with what matters.
        </p>

        <div 
          class="relative mx-auto w-[300px] h-[300px] cursor-pointer"
          @mouseenter="isHoveringImage = true"
          @mouseleave="isHoveringImage = false"
        >
          
          <img
            src="/images/Empty-box.png"
            alt="Empty Lost and Found Box"
            class="absolute inset-0 w-full h-full object-contain drop-shadow-xl
                   transition-opacity duration-1000 ease-in-out"
            :class="{ 'opacity-0': isHoveringImage }"
          />
          
          
          <img
            src="/images/lost-found-bg.png"
            alt="Full Lost and Found Box"
            class="absolute inset-0 w-full h-full object-contain drop-shadow-xl
                   transition-opacity duration-1000 ease-in-out"
            :class="{ 'opacity-100': isHoveringImage, 'opacity-0': !isHoveringImage }"
          />
        </div>
        </div>
    </div>

    <div class="flex h-1/2 w-full flex-col items-center justify-center bg-white p-10 text-center md:h-screen md:w-1/2">
      <div class="w-full max-w-sm">
        <h2 class="mb-3 text-4xl font-bold text-gray-800">
          Welcome!
        </h2>
        <p class="mb-8 text-base text-gray-600">
          Post items you've found or search for items you've lost. Sign in or create an account to get started.
        </p>

        <div class="flex flex-col gap-4">
          <RouterLink to="/login">
            <button class="w-full transform rounded-lg bg-[#3498db] px-8 py-3 text-base font-bold text-white transition hover:scale-105 hover:bg-[#2980b9]">
              Login
            </button>
          </RouterLink>
          <RouterLink to="/register">
            <button class="w-full transform rounded-lg bg-[#e67e22] px-8 py-3 text-base font-bold text-white transition hover:scale-105 hover:bg-[#d35400]">
              Register
            </button>
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Basic animations for a little flair */
@keyframes fade-in-down {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fade-in-up {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fade-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

.animate-fade-in-down {
  animation: fade-in-down 1.5s ease-out forwards;
}

.animate-fade-in-up {
  animation: fade-in-up 1.5s ease-out 0.3s forwards; /* slight delay */
}

/* Ensure images don't animate opacity twice at load if 'fade-in' is still present */
.animate-fade-in {
  animation: fade-in 1s ease-out 0.4s forwards; /* more delay for image */
}
</style>