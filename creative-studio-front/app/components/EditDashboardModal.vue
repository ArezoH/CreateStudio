<script setup lang="ts">
const props = defineProps<{
  dashboard: { id: string; name: string } | null
}>()

const open = defineModel<boolean>('open', { default: false })
const emit = defineEmits<{
  updated: []
}>()

const dashboardStore = useDashboardStore()
const toast = useToast()

const editingName = ref('')
const isUpdating = ref(false)

watch(open, (isOpen) => {
  if (isOpen && props.dashboard) {
    editingName.value = props.dashboard.name
  }
})

const handleUpdate = async () => {
  if (!props.dashboard || !editingName.value.trim()) return
  isUpdating.value = true
  try {
    await dashboardStore.updateDashboard(props.dashboard.id, editingName.value)
    toast.add({
      title: 'Updated',
      description: 'Dashboard name updated successfully.',
      color: 'success',
      icon: 'i-heroicons-check-circle',
    })
    open.value = false
    emit('updated')
  } catch (error) {
    toast.add({
      title: 'Error',
      description: 'Failed to update dashboard name.',
      color: 'error',
    })
  } finally {
    isUpdating.value = false
  }
}
</script>

<template>
  <UModal v-model:open="open">
    <template #header>
      <div class="flex items-center gap-3">
        <div class="p-2 rounded-lg bg-purple-100">
          <UIcon name="i-heroicons-pencil" class="w-5 h-5 text-purple-600" />
        </div>
        <h3 class="text-lg font-semibold">Edit Dashboard</h3>
      </div>
    </template>
    <template #body>
      <div class="p-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">Dashboard Name</label>
        <UInput
            v-model="editingName"
            placeholder="Dashboard name..."
            size="lg"
            autofocus
            @keydown.enter="handleUpdate"
        />
      </div>
    </template>
    <template #footer>
      <div class="flex justify-end gap-2">
        <UButton variant="ghost" @click="open = false">Cancel</UButton>
        <UButton
            color="primary"
            :loading="isUpdating"
            :disabled="!editingName.trim()"
            @click="handleUpdate"
        >
          Save
        </UButton>
      </div>
    </template>
  </UModal>
</template>