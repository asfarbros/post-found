// frontend/src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import PostFoundItemView from '../views/PostFoundItemView.vue'
import HomeView from '../views/HomeView.vue' 
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: HomeView }, // Default to login
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    { path: '/dashboard', name: 'dashboard', component: DashboardView, meta: { requiresAuth: true } },
    { path: '/post-found', name: 'post-found', component: PostFoundItemView, meta: { requiresAuth: true } }
  ]
})

// --- Use This Guard Logic ---
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const token = localStorage.getItem('token'); // Check for the token

  if (requiresAuth && !token) {
    // If route needs login BUT user has no token, send to login
    next({ name: 'login' });
  } else if (!requiresAuth && token && (to.name === 'login' || to.name === 'register' || to.name === 'home')) {
    // If route DOES NOT need login (like login, register, home) BUT user HAS token, send to dashboard
    next({ name: 'dashboard' });
  }
  else {
    // Otherwise allow navigation
    next();
  }
});

export default router