<script setup lang="ts">
defineProps<{
  title?: string
  message?: string
  confirmText?: string
  loading?: boolean
}>()

const open = defineModel<boolean>('open', { default: false })
const emit = defineEmits<{
  confirm: []
}>()
</script>

<template>
  <UModal v-model:open="open">
    <template #header>
      <div class="flex items-center gap-3">
        <div class="p-2 rounded-lg bg-red-100">
          <UIcon name="i-heroicons-exclamation-triangle" class="w-5 h-5 text-red-600" />
        </div>
        <h3 class="text-lg font-semibold">{{ title || 'Confirm Delete' }}</h3>
      </div>
    </template>
    <template #body>
      <div class="p-4">
        <p class="text-gray-600">{{ message || 'Are you sure? This action cannot be undone.' }}</p>
      </div>
    </template>
    <template #footer>
      <div class="flex justify-end gap-2">
        <UButton variant="ghost" @click="open = false">Cancel</UButton>
        <UButton
            color="error"
            :loading="loading"
            @click="emit('confirm')"
        >
          {{ confirmText || 'Delete' }}
        </UButton>
      </div>
    </template>
  </UModal>
</template>