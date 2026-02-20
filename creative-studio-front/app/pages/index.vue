<script setup lang="ts">
const authStore = useAuthStore()
const dashboardStore = useDashboardStore()
const toast = useToast()

const newDashboardName = ref('')
const isCreating = ref(false)

// Edit modal state
const editModalOpen = ref(false)
const editingDashboard = ref<{ id: string; name: string } | null>(null)

// Delete modal state
const deleteModalOpen = ref(false)
const deletingDashboard = ref<{ id: string; name: string } | null>(null)
const isDeleting = ref(false)

// Load dashboards when logged in
watch(
    () => authStore.isLoggedIn,
    async (isLoggedIn) => {
      if (isLoggedIn) {
        await dashboardStore.loadDashboards()
      }
    },
    { immediate: true },
)

onMounted(() => {
  authStore.init()
})

// Create dashboard
const handleCreateDashboard = async () => {
  if (!newDashboardName.value.trim()) return
  isCreating.value = true
  try {
    const dashboard = await dashboardStore.createDashboard(newDashboardName.value)
    if (dashboard) {
      toast.add({
        title: 'Dashboard Created',
        description: `"${dashboard.name}" was created successfully.`,
        color: 'success',
        icon: 'i-heroicons-check-circle',
      })
      newDashboardName.value = ''
      navigateTo(`/dashboard/${dashboard.id}`)
    }
  } catch (error) {
    toast.add({
      title: 'Error',
      description: 'Failed to create dashboard.',
      color: 'error',
    })
  } finally {
    isCreating.value = false
  }
}

// Edit modal
const openEditModal = (dashboard: any) => {
  editingDashboard.value = { id: dashboard.id, name: dashboard.name }
  editModalOpen.value = true
}

// Delete modal
const openDeleteModal = (dashboard: any) => {
  deletingDashboard.value = { id: dashboard.id, name: dashboard.name }
  deleteModalOpen.value = true
}

const handleDelete = async () => {
  if (!deletingDashboard.value) return
  isDeleting.value = true
  try {
    await dashboardStore.deleteDashboard(deletingDashboard.value.id)
    toast.add({
      title: 'Deleted',
      description: 'Dashboard was deleted.',
      color: 'success',
    })
    deleteModalOpen.value = false
  } catch (error) {
    toast.add({
      title: 'Error',
      description: 'Failed to delete dashboard.',
      color: 'error',
    })
  } finally {
    isDeleting.value = false
  }
}

// Format date
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = Math.abs(now.getTime() - date.getTime())
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  if (diffDays === 0) return 'Today'
  if (diffDays === 1) return 'Yesterday'
  if (diffDays < 7) return `${diffDays} days ago`
  return date.toLocaleDateString(undefined, { month: 'short', day: 'numeric' })
}

const features = [
  {
    title: 'Intuitive Drag-and-Drop',
    description: 'Effortlessly build beautiful layouts with our simple, powerful interface.',
    icon: 'i-heroicons-cursor-arrow-rays',
  },
  {
    title: 'Rich Content Editing',
    description: 'Write blog posts, notes, and content with a powerful rich text editor.',
    icon: 'i-heroicons-pencil-square',
  },
  {
    title: 'Organize Everything',
    description: 'Use todo lists, calendars, and notes to stay on top of your projects.',
    icon: 'i-heroicons-rectangle-group',
  },
]
</script>

<template>
  <main
      class="flex flex-col min-h-screen bg-gradient-to-br from-pink-100 via-purple-200 to-slate-600 relative"
  >
    <!-- Header -->
    <header class="fixed top-0 left-0 right-0 z-50 px-4 sm:px-6 py-3 bg-purple-900/80 backdrop-blur-sm">
      <div class="max-w-7xl mx-auto flex justify-between items-center">
        <div class="flex items-center space-x-2">
          <UIcon name="i-heroicons-sparkles" class="w-7 h-7 text-pink-300" />
          <span class="text-lg font-bold text-white">AI Creative Studio</span>
        </div>
        <div class="flex items-center gap-3">
          <template v-if="authStore.isLoggedIn">
            <span class="hidden sm:block text-white text-sm font-medium">
              {{ authStore.user?.name }}
            </span>
            <UButton color="neutral" variant="soft" size="sm" @click="authStore.logout()">
              Logout
            </UButton>
          </template>
          <template v-else>
            <UButton to="/login" color="neutral" variant="soft" size="sm">Login</UButton>
            <UButton to="/register" color="primary" size="sm">Sign Up</UButton>
          </template>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="flex-1 relative px-4 sm:px-6 pt-24 pb-12 sm:py-32">
      <div class="max-w-7xl mx-auto text-center">
        <div class="absolute inset-0 overflow-hidden pointer-events-none" aria-hidden="true">
          <div class="absolute top-1/4 left-1/4 w-32 sm:w-64 h-32 sm:h-64 bg-purple-600/20 rounded-full blur-3xl animate-pulse"></div>
          <div class="absolute bottom-1/4 right-1/4 w-32 sm:w-64 h-32 sm:h-64 bg-pink-600/20 rounded-full blur-3xl animate-pulse delay-1000"></div>
        </div>

        <div class="relative z-10">
          <h1 class="text-3xl sm:text-5xl md:text-7xl font-bold mb-4 sm:mb-6 leading-tight text-purple-900">
            Create Content with
            <span class="bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
              AI Magic
            </span>
          </h1>
          <p class="text-base sm:text-xl text-purple-800 mb-6 sm:mb-8 max-w-3xl mx-auto leading-relaxed">
            Build stunning content with drag-and-drop simplicity. The perfect platform for creators, marketers, and storytellers.
          </p>

          <!-- Logged in -->
          <template v-if="authStore.isLoggedIn">
            <p class="text-sm font-medium text-purple-800 bg-white/50 px-4 py-1.5 rounded-full backdrop-blur-sm inline-block mb-8">
              ✨ Welcome back, {{ authStore.user?.name || 'Creator' }}
            </p>

            <!-- Create Dashboard -->
            <div class="max-w-2xl mx-auto mb-8 p-4 sm:p-6 bg-white/40 backdrop-blur-sm rounded-2xl border border-white/50 shadow-lg text-left">
              <h2 class="text-lg sm:text-xl font-bold text-purple-900 mb-3">Create New Dashboard</h2>
              <form @submit.prevent="handleCreateDashboard" class="flex flex-col sm:flex-row gap-2 sm:gap-3">
                <UInput
                    v-model="newDashboardName"
                    placeholder="My awesome dashboard..."
                    size="lg"
                    class="flex-1"
                    :disabled="isCreating"
                />
                <UButton
                    type="submit"
                    color="primary"
                    size="lg"
                    icon="i-heroicons-plus"
                    :loading="isCreating"
                    :disabled="!newDashboardName.trim() || isCreating"
                    class="w-full sm:w-auto justify-center"
                >
                  Create
                </UButton>
              </form>
            </div>

            <!-- Dashboard List -->
            <div class="max-w-4xl mx-auto text-left">
              <div v-if="dashboardStore.isLoading" class="flex justify-center py-8">
                <UIcon name="i-heroicons-arrow-path" class="w-8 h-8 text-purple-600 animate-spin" />
              </div>

              <div v-else-if="dashboardStore.dashboards.length > 0">
                <h3 class="text-lg font-semibold text-purple-900 mb-4">
                  Your Dashboards ({{ dashboardStore.dashboards.length }})
                </h3>
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                  <article
                      v-for="dashboard in dashboardStore.dashboards"
                      :key="dashboard.id"
                      class="group relative bg-white/50 backdrop-blur-sm rounded-xl border border-white/50 hover:bg-white/70 hover:shadow-xl transition-all duration-300"
                  >
                    <NuxtLink :to="`/dashboard/${dashboard.id}`" class="block p-4 sm:p-5">
                      <div class="flex items-start gap-3">
                        <div class="w-10 h-10 sm:w-12 sm:h-12 bg-gradient-to-br from-purple-500 to-pink-500 rounded-lg flex items-center justify-center shadow-md flex-shrink-0">
                          <UIcon name="i-heroicons-squares-2x2" class="w-5 h-5 sm:w-6 sm:h-6 text-white" />
                        </div>
                        <div class="min-w-0 flex-1">
                          <h4 class="text-base sm:text-lg font-semibold text-purple-900 group-hover:text-purple-700 transition-colors line-clamp-2 mb-1">
                            {{ dashboard.name }}
                          </h4>
                          <p class="text-xs sm:text-sm text-purple-600">
                            Created {{ formatDate(dashboard.createdAt) }}
                          </p>
                        </div>
                      </div>
                    </NuxtLink>
                    <div class="flex items-center justify-end gap-2 px-4 pb-3">
                      <UButton
                          icon="i-heroicons-pencil"
                          size="xs"
                          color="neutral"
                          variant="soft"
                          @click.prevent.stop="openEditModal(dashboard)"
                      />
                      <UButton
                          icon="i-heroicons-trash"
                          size="xs"
                          color="error"
                          variant="soft"
                          @click.prevent.stop="openDeleteModal(dashboard)"
                      />
                    </div>
                  </article>
                </div>
              </div>

              <div v-else class="text-center py-12 bg-white/30 rounded-2xl border border-white/40 backdrop-blur-sm">
                <div class="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <UIcon name="i-heroicons-squares-2x2" class="w-8 h-8 text-purple-500" />
                </div>
                <h3 class="text-lg font-semibold text-purple-900 mb-2">No dashboards yet</h3>
                <p class="text-purple-700">Create your first dashboard above to get started!</p>
              </div>
            </div>
          </template>

          <!-- Not logged in -->
          <template v-else>
            <div class="flex flex-col sm:flex-row gap-4 justify-center items-center mb-16">
              <UButton to="/register" size="xl" color="primary" icon="i-heroicons-rocket-launch" class="px-8">
                Get Started Free
              </UButton>
              <UButton to="/login" size="xl" variant="outline" class="px-8">
                Sign In
              </UButton>
            </div>
          </template>

          <!-- Features -->
          <section class="mt-16">
            <h2 class="text-2xl sm:text-3xl font-bold text-purple-900 mb-8 sm:mb-12">Powerful Features</h2>
            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 sm:gap-8">
              <article
                  v-for="feature in features"
                  :key="feature.title"
                  class="p-4 sm:p-6 bg-white/30 rounded-xl border border-white/40 hover:bg-white/40 transition-all duration-300 hover:scale-105 backdrop-blur-sm"
              >
                <div class="w-10 h-10 sm:w-12 sm:h-12 bg-gradient-to-br from-purple-500 to-pink-500 rounded-lg flex items-center justify-center mb-3 sm:mb-4 mx-auto shadow-md">
                  <UIcon :name="feature.icon" class="w-5 h-5 sm:w-6 sm:h-6 text-white" />
                </div>
                <h3 class="text-lg sm:text-xl font-semibold mb-2 text-purple-900">{{ feature.title }}</h3>
                <p class="text-sm sm:text-base text-purple-800">{{ feature.description }}</p>
              </article>
            </div>
          </section>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-purple-900/90 text-white py-8 sm:py-12 px-4 sm:px-6">
      <div class="max-w-7xl mx-auto">
        <div class="flex flex-col items-center justify-center space-y-2 mb-4">
          <UIcon name="i-heroicons-sparkles" class="w-6 h-6 sm:w-8 sm:h-8 text-pink-300" />
          <span class="text-lg sm:text-xl font-bold">AI Creative Studio</span>
          <p class="text-sm text-purple-200 text-center">Creating magical content experiences with powerful tools.</p>
        </div>
        <div class="mt-6 pt-6 border-t border-purple-700 text-center text-purple-300 text-sm">
          <p>&copy; 2025 AI Creative Studio. All rights reserved.</p>
        </div>
      </div>
    </footer>

    <!-- Edit Modal -->
    <EditDashboardModal
        v-model:open="editModalOpen"
        :dashboard="editingDashboard"
    />

    <!-- Delete Modal -->
    <DeleteConfirmModal
        v-model:open="deleteModalOpen"
        title="Delete Dashboard"
        :message="`Are you sure you want to delete '${deletingDashboard?.name}'? All widgets inside will be permanently deleted.`"
        confirm-text="Delete Dashboard"
        :loading="isDeleting"
        @confirm="handleDelete"
    />
  </main>
</template>