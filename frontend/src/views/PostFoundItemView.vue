<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const API_URL = 'http://localhost:8080/api/items'; // Backend endpoint
const router = useRouter();

// Form fields
const itemName = ref('');
const description = ref('');
const locationFound = ref('');
const imageFile = ref(null);
const postedBy = ref(localStorage.getItem('userName') || ''); // ✅ auto-link logged-in user

// State flags
const error = ref(null);
const successMessage = ref('');
const isLoading = ref(false);

// Handle file input
function handleFileChange(event) {
  const file = event.target.files[0];
  if (file) {
    imageFile.value = file;
  }
}

// --- CREATE ITEM ---
async function createItem() {
  if (!itemName.value || !description.value || !locationFound.value) {
    error.value = "Please fill in all required fields.";
    return;
  }

  if (!postedBy.value) {
    error.value = "You must be logged in to post an item.";
    return;
  }

  isLoading.value = true;
  error.value = null;
  successMessage.value = '';

  // Prepare form data
  const formData = new FormData();
  formData.append('itemName', itemName.value);
  formData.append('description', description.value);
  formData.append('locationFound', locationFound.value);
  formData.append('postedBy', postedBy.value); // ✅ send username automatically
  if (imageFile.value) {
    formData.append('imageFile', imageFile.value);
  }

  try {
    const response = await fetch(API_URL, {
      method: 'POST',
      body: formData
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(`Failed to create item: ${errorData || response.statusText}`);
    }

    successMessage.value = 'Item posted successfully!';
    itemName.value = '';
    description.value = '';
    locationFound.value = '';
    imageFile.value = null;
    document.getElementById('imageUpload').value = null;

    // Redirect after short delay
    setTimeout(() => {
      router.push({ name: 'dashboard' });
    }, 1500);
  } catch (err) {
    console.error(err);
    error.value = err.message;
  } finally {
    isLoading.value = false;
  }
}
</script>

<template>
  <div class="bg-gray-100 min-h-screen py-10">
    <div class="max-w-[600px] mx-auto p-[30px] bg-white rounded-xl shadow-[0_4px_15px_rgba(0,0,0,0.1)] text-[#333]">
      <h1 class="text-center mb-[15px] text-[#333] text-3xl font-bold">Post a New Found Item</h1>
      <p class="text-center text-[0.95rem] text-[#555] mb-5">
        Logged in as: <strong>{{ postedBy }}</strong>
      </p>

      <div v-if="error" class="p-3 rounded-md mb-[15px] text-center bg-[#fdecea] text-[#e74c3c]">
        <strong>Error:</strong> {{ error }}
      </div>
      <div v-if="successMessage" class="p-3 rounded-md mb-[15px] text-center bg-[#eafaf1] text-[#2ecc71]">
        {{ successMessage }}
      </div>

      <form @submit.prevent="createItem" class="flex flex-col gap-5">
        <div class="flex flex-col">
          <label for="itemName" class="font-bold mb-1.5 text-[#555]">Item Name *</label>
          <input
            id="itemName"
            v-model="itemName"
            placeholder="e.g., Red Water Bottle"
            required
            class="w-full px-3 py-2.5 border border-[#ccc] rounded-md text-base focus:border-blue-500 focus:ring-1 focus:ring-blue-500 outline-none"
          />
        </div>

        <div class="flex flex-col">
          <label for="description" class="font-bold mb-1.5 text-[#555]">Description *</label>
          <textarea
            id="description"
            v-model="description"
            placeholder="e.g., Found near library, with a Nike logo"
            rows="3"
            required
            class="w-full px-3 py-2.5 border border-[#ccc] rounded-md text-base focus:border-blue-500 focus:ring-1 focus:ring-blue-500 outline-none"
          ></textarea>
        </div>

        <div class="flex flex-col">
          <label for="locationFound" class="font-bold mb-1.5 text-[#555]">Location Found *</label>
          <input
            id="locationFound"
            v-model="locationFound"
            placeholder="e.g., Canteen, Library Floor 2"
            required
            class="w-full px-3 py-2.5 border border-[#ccc] rounded-md text-base focus:border-blue-500 focus:ring-1 focus:ring-blue-500 outline-none"
          />
        </div>

        <div class="flex flex-col">
          <label for="imageUpload" class="font-bold mb-1.5 text-[#555]">Upload Image (Optional)</label>
          <input
            id="imageUpload"
            type="file"
            accept="image/*"
            @change="handleFileChange"
            class="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
          />
          <small class="text-sm text-gray-500 mt-1">Upload a picture of the found item.</small>
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="w-full py-3 px-4 border-none rounded-md text-base font-bold text-white bg-[#3498db] cursor-pointer transition duration-200 ease-in-out hover:bg-[#2980b9] disabled:bg-[#7f8c8d] disabled:cursor-not-allowed"
        >
          {{ isLoading ? 'Posting...' : 'Post Item' }}
        </button>

        <button
          type="button"
          @click="router.push({ name: 'dashboard' })"
          class="w-full py-3 px-4 border-none rounded-md text-base font-bold text-white bg-[#7f8c8d] cursor-pointer transition duration-200 ease-in-out hover:bg-[#606d6e]"
        >
          Cancel
        </button>
      </form>
    </div>
  </div>
</template>