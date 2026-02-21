<script setup lang="ts">
definePageMeta({
  middleware: 'auth',
})

const route = useRoute()
const router = useRouter()
const dashboardStore = useDashboardStore()
const toast = useToast()

const dashboardId = computed(() => route.params.id as string)

// Load dashboard on mount
onMounted(async () => {
  const authStore = useAuthStore()
  authStore.init()

  if (!authStore.isLoggedIn) {
    return navigateTo('/login')
  }

  await dashboardStore.loadDashboard(dashboardId.value)

  if (!dashboardStore.currentDashboard) {
    toast.add({ title: 'Dashboard not found', color: 'error' })
    return navigateTo('/')
  }
})

// Widget rename handler
const handleRename = (widgetId: string, newName: string) => {
  dashboardStore.updateWidget(widgetId, { name: newName })
}

// Widget remove handler
const handleRemove = (widgetId: string) => {
  dashboardStore.removeWidget(widgetId)
}

// Sidebar state (mobile toggle)
const sidebarOpen = ref(false)

// Get widget icon/color from widgetTypes
const getWidgetMeta = (type: string) => {
  return dashboardStore.widgetTypes.find(wt => wt.id === type) || {
    icon: 'i-heroicons-cube',
    color: 'from-gray-500 to-gray-600',
    name: 'Widget',
  }
}
</script>

<template>
  <div class="flex flex-col h-screen bg-gradient-to-br from-pink-50 via-purple-50 to-slate-100">
    <!-- Top Bar -->
    <header class="flex items-center gap-3 px-4 sm:px-6 py-3 bg-white/80 backdrop-blur-sm border-b border-gray-200/60 z-30">
      <UButton
          icon="i-heroicons-arrow-left"
          color="neutral"
          variant="ghost"
          size="sm"
          @click="router.push('/')"
          title="Back to dashboards"
      />

      <div class="flex-1 min-w-0">
        <h1 class="text-lg font-bold text-gray-900 truncate">
          {{ dashboardStore.currentDashboard?.name || 'Loading...' }}
        </h1>
        <p class="text-xs text-gray-500">
          {{ dashboardStore.widgets.length }} widget{{ dashboardStore.widgets.length !== 1 ? 's' : '' }}
        </p>
      </div>

      <!-- Mobile sidebar toggle -->
      <UButton
          icon="i-heroicons-plus-circle"
          color="primary"
          size="sm"
          class="sm:hidden"
          @click="sidebarOpen = true"
      >
        Add
      </UButton>
    </header>

    <!-- Loading state -->
    <div v-if="dashboardStore.isLoading" class="flex-1 flex items-center justify-center">
      <div class="text-center">
        <UIcon name="i-heroicons-arrow-path" class="w-10 h-10 text-pink-500 animate-spin mx-auto mb-3" />
        <p class="text-gray-500 text-sm">Loading dashboard...</p>
      </div>
    </div>

    <!-- Main Content -->
    <div v-else class="flex-1 flex overflow-hidden">
      <!-- Sidebar (desktop) -->
      <aside class="hidden sm:flex flex-col w-56 lg:w-64 p-4 border-r border-gray-200/60 bg-white/50 backdrop-blur-sm overflow-y-auto">
        <DashboardWidgetLibrary />
      </aside>

      <!-- Mobile Sidebar Drawer -->
      <USlideover v-model:open="sidebarOpen" side="left" title="Add Widgets">
        <template #body>
          <div class="p-4">
            <DashboardWidgetLibrary />
          </div>
        </template>
      </USlideover>

      <!-- Widget Canvas -->
      <main class="flex-1 overflow-auto p-4 sm:p-6">
        <!-- Empty state -->
        <div
            v-if="dashboardStore.widgets.length === 0"
            class="flex flex-col items-center justify-center h-full text-center"
        >
          <div class="w-20 h-20 bg-gradient-to-br from-pink-100 to-purple-100 rounded-2xl flex items-center justify-center mb-4">
            <UIcon name="i-heroicons-squares-2x2" class="w-10 h-10 text-purple-400" />
          </div>
          <h2 class="text-xl font-semibold text-gray-800 mb-2">No widgets yet</h2>
          <p class="text-sm text-gray-500 mb-4 max-w-sm">
            Add widgets from the sidebar to start building your workspace.
          </p>
          <UButton
              color="primary"
              icon="i-heroicons-plus"
              class="sm:hidden"
              @click="sidebarOpen = true"
          >
            Add Widget
          </UButton>
        </div>

        <!-- Widget Grid -->
        <div
            v-else
            class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4 sm:gap-6 auto-rows-[minmax(320px,auto)]"
        >
          <div
              v-for="widget in dashboardStore.widgets"
              :key="widget.id"
              class="min-h-[320px]"
          >
            <WidgetsBaseWidget
                :widget-id="widget.id"
                :name="widget.name"
                :type="widget.type"
                :icon="getWidgetMeta(widget.type).icon"
                :color="getWidgetMeta(widget.type).color"
                @rename="(name) => handleRename(widget.id, name)"
                @remove="handleRemove(widget.id)"
            >
              <!-- Content Editor -->
              <WidgetsContentEditor
                  v-if="widget.type === 'content-editor'"
                  :widget-id="widget.id"
                  :data="widget.data"
              />

              <!-- Todo List -->
              <WidgetsTodoList
                  v-if="widget.type === 'todo-list'"
                  :widget-id="widget.id"
                  :data="widget.data"
              />

              <!-- Quick Notes -->
              <WidgetsQuickNotes
                  v-if="widget.type === 'notes-widget'"
                  :widget-id="widget.id"
                  :data="widget.data"
              />
            </WidgetsBaseWidget>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>