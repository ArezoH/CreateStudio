<script setup lang="ts">
const props = defineProps<{
  widgetId: string
  name: string
  type: string
  icon: string
  color?: string
}>()

const emit = defineEmits<{
  rename: [name: string]
  remove: []
}>()

const dashboardStore = useDashboardStore()
const toast = useToast()

// Rename state
const isEditing = ref(false)
const editName = ref(props.name)

const startEditing = () => {
  editName.value = props.name
  isEditing.value = true
  nextTick(() => {
    const input = document.querySelector(`#rename-${props.widgetId}`) as HTMLInputElement
    input?.focus()
    input?.select()
  })
}

const confirmRename = () => {
  const trimmed = editName.value.trim()
  if (trimmed && trimmed !== props.name) {
    emit('rename', trimmed)
    toast.add({ title: 'Widget Renamed', color: 'success', icon: 'i-heroicons-check-circle' })
  }
  isEditing.value = false
}

const cancelEditing = () => {
  editName.value = props.name
  isEditing.value = false
}

// Delete confirmation
const showDeleteConfirm = ref(false)

const handleDelete = () => {
  emit('remove')
  showDeleteConfirm.value = false
  toast.add({ title: 'Widget Removed', color: 'success', icon: 'i-heroicons-trash' })
}

// Selected state
const isSelected = computed(() => dashboardStore.selectedWidget === props.widgetId)

const selectWidget = () => {
  dashboardStore.selectedWidget = isSelected.value ? null : props.widgetId
}
</script>

<template>
  <div
      class="flex flex-col bg-white/70 backdrop-blur-sm rounded-2xl border shadow-sm hover:shadow-md transition-all duration-200 overflow-hidden h-full"
      :class="isSelected ? 'border-pink-400 ring-2 ring-pink-200' : 'border-white/60'"
      @click.self="selectWidget"
  >
    <!-- Widget Header -->
    <div class="flex items-center gap-2 px-4 py-2.5 border-b border-gray-100 bg-white/50">
      <div
          class="w-7 h-7 rounded-lg flex items-center justify-center bg-gradient-to-br flex-shrink-0"
          :class="color || 'from-pink-500 to-purple-600'"
      >
        <UIcon :name="icon" class="w-4 h-4 text-white" />
      </div>

      <!-- Name display / edit -->
      <div class="flex-1 min-w-0">
        <input
            v-if="isEditing"
            :id="`rename-${widgetId}`"
            v-model="editName"
            class="w-full text-sm font-semibold bg-white border border-pink-300 rounded-md px-2 py-0.5 focus:outline-none focus:ring-2 focus:ring-pink-400"
            maxlength="40"
            @keydown.enter="confirmRename"
            @keydown.escape="cancelEditing"
            @blur="confirmRename"
        />
        <p v-else class="text-sm font-semibold text-gray-800 truncate cursor-pointer" @dblclick="startEditing">
          {{ name }}
        </p>
      </div>

      <!-- Actions -->
      <div class="flex items-center gap-0.5 flex-shrink-0">
        <UButton
            icon="i-heroicons-pencil"
            size="xs"
            color="neutral"
            variant="ghost"
            @click="startEditing"
            title="Rename"
        />
        <UButton
            icon="i-heroicons-trash"
            size="xs"
            color="error"
            variant="ghost"
            @click="showDeleteConfirm = true"
            title="Delete"
        />
      </div>
    </div>

    <!-- Widget Content -->
    <div class="flex-1 overflow-auto">
      <slot />
    </div>

    <!-- Delete Confirmation Modal -->
    <UModal v-model:open="showDeleteConfirm">
      <template #content>
        <div class="p-6">
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Delete Widget</h3>
          <p class="text-sm text-gray-600 mb-6">
            Are you sure you want to delete <strong>"{{ name }}"</strong>? This action cannot be undone.
          </p>
          <div class="flex justify-end gap-2">
            <UButton color="neutral" variant="soft" @click="showDeleteConfirm = false">Cancel</UButton>
            <UButton color="error" @click="handleDelete">Delete</UButton>
          </div>
        </div>
      </template>
    </UModal>
  </div>
</template>