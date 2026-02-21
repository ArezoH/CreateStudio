<script setup lang="ts">
const dashboardStore = useDashboardStore()
const toast = useToast()
const isAdding = ref<string | null>(null)

const handleAdd = async (typeId: string) => {
  isAdding.value = typeId
  try {
    const widget = await dashboardStore.addWidget(typeId)
    if (widget) {
      toast.add({
        title: 'Widget Added',
        description: `${widget.name} has been added to your dashboard.`,
        color: 'success',
        icon: 'i-heroicons-plus-circle',
      })
    }
  } catch (e) {
    toast.add({
      title: 'Error',
      description: 'Failed to add widget.',
      color: 'error',
    })
  } finally {
    isAdding.value = null
  }
}
</script>

<template>
  <div class="space-y-2">
    <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider px-1">Add Widgets</h3>
    <div class="space-y-1.5">
      <button
          v-for="wt in dashboardStore.widgetTypes"
          :key="wt.id"
          class="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl border border-transparent hover:border-pink-200 hover:bg-pink-50/50 transition-all duration-200 text-left group"
          :disabled="isAdding === wt.id"
          @click="handleAdd(wt.id)"
      >
        <div
            class="w-9 h-9 rounded-lg flex items-center justify-center bg-gradient-to-br shadow-sm group-hover:scale-110 transition-transform"
            :class="wt.color"
        >
          <UIcon :name="wt.icon" class="w-4.5 h-4.5 text-white" />
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-gray-800">{{ wt.name }}</p>
        </div>
        <UIcon
            v-if="isAdding === wt.id"
            name="i-heroicons-arrow-path"
            class="w-4 h-4 text-pink-500 animate-spin"
        />
        <UIcon
            v-else
            name="i-heroicons-plus-circle"
            class="w-5 h-5 text-gray-300 group-hover:text-pink-500 transition-colors"
        />
      </button>
    </div>
  </div>
</template>