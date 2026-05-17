<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'

const API_URL = 'http://localhost:8080/api/items'
const route = useRoute()

// --- States ---
const items = ref([])
const loading = ref(true)
const error = ref(null)
const claiming = ref(false)
const successMessage = ref('')
const activeTab = ref('community') // 'community' | 'posted' | 'claimed'
const currentUser = localStorage.getItem('userName') || ''

// ✅ NEW: State to hold the URL of the image to be zoomed
const zoomedImageUrl = ref(null)

// --- Fetch all items from backend ---
async function fetchItems() {
  try {
    loading.value = true
    const response = await axios.get(API_URL)
    items.value = response.data
  } catch (err) {
    console.error(err)
    error.value = 'Failed to load items.'
  } finally {
    loading.value = false
  }
}

// --- Claim item ---
async function claimItem(id) {
  if (!currentUser) {
    alert('You must be logged in to claim an item.')
    return
  }

  claiming.value = true
  try {
    await axios.put(`${API_URL}/${id}/claim?claimedBy=${encodeURIComponent(currentUser)}`)
    successMessage.value = 'Item claimed successfully!'
    await fetchItems()
  } catch (err) {
    console.error(err)
    error.value = 'Failed to claim item.'
  } finally {
    claiming.value = false
    setTimeout(() => (successMessage.value = ''), 3000)
  }
}

// --- Computed properties ---
const unclaimedItems = computed(() => items.value.filter(i => i.status === 'FOUND'))
const claimedItems = computed(() => items.value.filter(i => i.status === 'CLAIMED'))
const myPostedItems = computed(() => items.value.filter(i => i.postedBy === currentUser))
const myClaimedItems = computed(() => items.value.filter(i => i.claimedBy === currentUser))

// --- On mount ---
onMounted(() => {
  fetchItems()
  if (route.query.tab === 'my') {
    activeTab.value = 'posted'
  }
})
</script>

<template>
  <div class="text-center text-[#333] p-5 bg-[#f9fafc] rounded-xl shadow-[0_4px_12px_rgba(0,0,0,0.1)] min-h-screen">
    
    <div class="flex justify-center gap-2.5 mb-[25px]">
      <button
        :class="[
          'border-none py-2.5 px-5 rounded-lg text-[#2c3e50] cursor-pointer font-bold transition-all duration-200',
          activeTab === 'community' ? 'bg-[#3498db] text-white shadow-[0_0_8px_rgba(52,152,219,0.4)] hover:bg-[#3498db]' : 'bg-[#ecf0f1] hover:bg-[#dfe6e9]'
        ]"
        @click="activeTab = 'community'"
      >
        🌍 All Items
      </button>
      <button
        :class="[
          'border-none py-2.5 px-5 rounded-lg text-[#2c3e50] cursor-pointer font-bold transition-all duration-200',
          activeTab === 'posted' ? 'bg-[#3498db] text-white shadow-[0_0_8px_rgba(52,152,219,0.4)] hover:bg-[#3498db]' : 'bg-[#ecf0f1] hover:bg-[#dfe6e9]'
        ]"
        @click="activeTab = 'posted'"
      >
        📸 Posted By Me
      </button>
      <button
        :class="[
          'border-none py-2.5 px-5 rounded-lg text-[#2c3e50] cursor-pointer font-bold transition-all duration-200',
          activeTab === 'claimed' ? 'bg-[#3498db] text-white shadow-[0_0_8px_rgba(52,152,219,0.4)] hover:bg-[#3498db]' : 'bg-[#ecf0f1] hover:bg-[#dfe6e9]'
        ]"
        @click="activeTab = 'claimed'"
      >
        📦 Claimed By Me
      </button>
    </div>

    <p v-if="loading" class="mt-5 text-[1.05rem]">Loading items...</p>
    <p v-if="error" class="mt-5 text-[1.05rem] text-[#e74c3c]">{{ error }}</p>
    <p v-if="successMessage" class="mt-5 text-[1.05rem] text-[#27ae60]">{{ successMessage }}</p>

    <div v-if="activeTab === 'community' && !loading">
      <div v-if="unclaimedItems.length === 0 && claimedItems.length === 0" class="mt-5 text-[1.05rem] text-[#7f8c8d] italic">
        No items have been posted yet.
      </div>
      
      <div v-if="unclaimedItems.length > 0" class="mt-4">
        <h3 class="text-2xl font-bold mb-4 text-left">🟦 Found Items (Unclaimed)</h3>
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5 mt-[15px]">
          <div
            v-for="item in unclaimedItems"
            :key="item.id"
            class="bg-white rounded-[10px] p-[15px] shadow-[0_4px_10px_rgba(0,0,0,0.1)] transition duration-300 text-left hover:scale-[1.02] flex flex-col"
          >
            <img 
              v-if="item.imageUrl" 
              :src="item.imageUrl" 
              class="w-full aspect-square object-cover rounded-lg mb-2.5 cursor-zoom-in" 
              @click="zoomedImageUrl = item.imageUrl" 
            />
            <div class="item-content flex flex-col flex-1">
              <h4 class="text-[#2c3e50] mb-[5px] text-xl font-bold">{{ item.itemName }}</h4>
              <p class="text-[#555] mb-2 text-sm flex-1">{{ item.description }}</p>
              <p class="text-sm mb-1"><strong>📍 Location:</strong> {{ item.locationFound }}</p>
              <p class="text-sm mb-1"><strong>👤 Posted By:</strong> {{ item.postedBy || 'Unknown' }}</p>
              <button
                v-if="item.postedBy !== currentUser"
                class="w-full bg-[#f39c12] border-none py-2.5 rounded-md text-white font-bold cursor-pointer transition duration-200 mt-2.5 hover:enabled:bg-[#e67e22] disabled:bg-[#bdc3c7]"
                @click="claimItem(item.id)"
                :disabled="claiming"
              >
                {{ claiming ? 'Claiming...' : 'Claim Item' }}
              </button>
              <p v-else class="text-[#7f8c8d] italic text-center mt-2.5 text-sm">🧍 This is your post</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="claimedItems.length > 0" class="mt-8">
        <h3 class="text-2xl font-bold mb-4 text-left">🟩 Claimed Items</h3>
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5 mt-[15px]">
          <div
            v-for="item in claimedItems"
            :key="item.id"
            class="bg-white rounded-[10px] p-[15px] shadow-[0_4px_10px_rgba(0,0,0,0.1)] transition duration-300 text-left hover:scale-[1.02] border-l-[5px] border-l-[#2ecc71] opacity-70"
          >
            <img 
              v-if="item.imageUrl" 
              :src="item.imageUrl" 
              class="w-full aspect-square object-cover rounded-lg mb-2.5 cursor-zoom-in" 
              @click="zoomedImageUrl = item.imageUrl" 
            />
            <div class="item-content">
              <h4 class="text-[#2c3e50] mb-[5px] text-xl font-bold">{{ item.itemName }}</h4>
              <p class="text-[#555] mb-2 text-sm">{{ item.description }}</p>
              <p class="text-sm mb-1"><strong>📍 Location:</strong> {{ item.locationFound }}</p>
              <p class="text-sm mb-1"><strong>👤 Posted By:</strong> {{ item.postedBy }}</p>
              <p class="text-sm mb-1"><strong>✅ Claimed By:</strong> {{ item.claimedBy }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="activeTab === 'posted' && !loading">
      <h3 class="text-2xl font-bold mb-4 text-left">📸 My Posted Items</h3>
      <div v-if="myPostedItems.length === 0" class="mt-5 text-[1.05rem] text-[#7f8c8d] italic">
        You haven’t posted any items yet.
      </div>
      <div v-if="myPostedItems.length > 0" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5 mt-[15px]">
        <div
          v-for="item in myPostedItems"
          :key="item.id"
          :class="[
            'bg-white rounded-[10px] p-[15px] shadow-[0_4px_10px_rgba(0,0,0,0.1)] transition duration-300 text-left hover:scale-[1.02]',
            item.status === 'CLAIMED' && 'border-l-[5px] border-l-[#2ecc71] opacity-70'
          ]"
        >
          <img 
            v-if="item.imageUrl" 
            :src="item.imageUrl" 
            class="w-full aspect-square object-cover rounded-lg mb-2.5 cursor-zoom-in" 
            @click="zoomedImageUrl = item.imageUrl" 
          />
          <div class="item-content">
            <h4 class="text-[#2c3e50] mb-[5px] text-xl font-bold">{{ item.itemName }}</h4>
            <p class="text-[#555] mb-2 text-sm">{{ item.description }}</p>
            <p class="text-sm mb-1"><strong>Status:</strong> {{ item.status }}</p>
            <p class="text-sm mb-1"><strong>📍 Location:</strong> {{ item.locationFound }}</p>
            <p class="text-sm mb-1"><strong>✅ Claimed By:</strong> {{ item.claimedBy || 'Not yet claimed' }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="activeTab === 'claimed' && !loading">
       <h3 class="text-2xl font-bold mb-4 text-left">📦 My Claimed Items</h3>
       <div v-if="myClaimedItems.length === 0" class="mt-5 text-[1.05rem] text-[#7f8c8d] italic">
        You haven’t claimed any items yet.
      </div>
       <div v-if="myClaimedItems.length > 0" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5 mt-[15px]">
         <div
            v-for="item in myClaimedItems"
            :key="item.id"
            class="bg-white rounded-[10px] p-[15px] shadow-[0_4px_10px_rgba(0,0,0,0.1)] transition duration-300 text-left hover:scale-[1.02] border-l-[5px] border-l-[#2ecc71] opacity-70"
          >
            <img 
              v-if="item.imageUrl" 
              :src="item.imageUrl" 
              class="w-full aspect-square object-cover rounded-lg mb-2.5 cursor-zoom-in" 
              @click="zoomedImageUrl = item.imageUrl" 
            />
            <div class="item-content">
              <h4 class="text-[#2c3e50] mb-[5px] text-xl font-bold">{{ item.itemName }}</h4>
              <p class="text-[#555] mb-2 text-sm">{{ item.description }}</p>
              <p class="text-sm mb-1"><strong>📍 Location:</strong> {{ item.locationFound }}</p>
              <p class="text-sm mb-1"><strong>👤 Posted By:</strong> {{ item.postedBy }}</p>
            </div>
          </div>
       </div>
    </div>

  </div>
  
  <div
    v-if="zoomedImageUrl"
    class="fixed inset-0 z-50 flex cursor-pointer items-center justify-center p-4"
    style="background-color: rgba(0, 0, 0, 0.85);" @click="zoomedImageUrl = null"
  >
    <img
      :src="zoomedImageUrl"
      alt="Zoomed item image"
      class="block max-h-full max-w-full rounded-lg shadow-xl"
      @click.stop
    />
  </div>

</template>