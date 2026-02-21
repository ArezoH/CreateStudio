<script setup lang="ts">
const props = defineProps<{
  widgetId: string
  data: Record<string, any>
}>()

const dashboardStore = useDashboardStore()

const noteText = ref(props.data?.text || '')
const isSaving = ref(false)
const lastSaved = ref<Date | null>(null)

// Auto-save with debounce
let saveTimeout: ReturnType<typeof setTimeout> | null = null

const saveNote = () => {
  if (saveTimeout) clearTimeout(saveTimeout)
  isSaving.value = true

  saveTimeout = setTimeout(async () => {
    await dashboardStore.updateWidgetData(props.widgetId, {
      ...props.data,
      text: noteText.value,
    })
    isSaving.value = false
    lastSaved.value = new Date()
  }, 600)
}

watch(noteText, () => saveNote())

const charCount = computed(() => noteText.value.length)
const lineCount = computed(() => noteText.value.split('\n').length)

const savedLabel = computed(() => {
  if (isSaving.value) return 'Saving...'
  if (lastSaved.value) {
    const seconds = Math.floor((Date.now() - lastSaved.value.getTime()) / 1000)
    if (seconds < 5) return 'Saved'
    if (seconds < 60) return `Saved ${seconds}s ago`
    return `Saved ${Math.floor(seconds / 60)}m ago`
  }
  return ''
})

// Update savedLabel reactively
const now = ref(Date.now())
let interval: ReturnType<typeof setInterval> | null = null
onMounted(() => {
  interval = setInterval(() => { now.value = Date.now() }, 5000)
})

onBeforeUnmount(() => {
  if (saveTimeout) clearTimeout(saveTimeout)
  if (interval) clearInterval(interval)
})
</script>

<template>
  <div class="flex flex-col h-full">
    <textarea
        v-model="noteText"
        placeholder="Write your notes here..."
        class="flex-1 w-full resize-none border-none bg-transparent px-4 py-3 text-sm text-gray-800 focus:outline-none focus:ring-0 placeholder-gray-400"
        spellcheck="true"
    />

    <!-- Footer -->
    <div class="flex items-center justify-between px-3 py-1.5 border-t border-gray-100 text-xs text-gray-400">
      <span>{{ lineCount }} lines · {{ charCount }} chars</span>
      <span v-if="savedLabel" class="flex items-center gap-1">
        <UIcon
            :name="isSaving ? 'i-heroicons-arrow-path' : 'i-heroicons-check-circle'"
            class="w-3 h-3"
            :class="isSaving ? 'animate-spin' : 'text-green-500'"
        />
        {{ savedLabel }}
      </span>
    </div>
  </div>
</template>