<script setup lang="ts">
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Underline from '@tiptap/extension-underline'
import TextAlign from '@tiptap/extension-text-align'
import Highlight from '@tiptap/extension-highlight'
import CharacterCount from '@tiptap/extension-character-count'

const props = defineProps<{
  widgetId: string
  data: Record<string, any>
}>()

const dashboardStore = useDashboardStore()

// Auto-save debounce
let saveTimeout: ReturnType<typeof setTimeout> | null = null
const isSaving = ref(false)
const lastSaved = ref<Date | null>(null)

const saveContent = (content: string) => {
  if (saveTimeout) clearTimeout(saveTimeout)
  isSaving.value = true

  saveTimeout = setTimeout(async () => {
    await dashboardStore.updateWidgetData(props.widgetId, {
      ...props.data,
      content,
    })
    isSaving.value = false
    lastSaved.value = new Date()
  }, 800)
}

const editor = useEditor({
  content: props.data?.content || '<p>Start writing...</p>',
  extensions: [
    StarterKit,
    Underline,
    Highlight.configure({ multicolor: true }),
    TextAlign.configure({ types: ['heading', 'paragraph'] }),
    CharacterCount,
  ],
  editorProps: {
    attributes: {
      class: 'prose prose-sm max-w-none focus:outline-none min-h-[120px] px-4 py-3',
    },
  },
  onUpdate: ({ editor }) => {
    saveContent(editor.getHTML())
  },
})

// Toolbar actions
const toolbarItems = computed(() => {
  if (!editor.value) return []
  return [
    { icon: 'i-heroicons-bold', action: () => editor.value?.chain().focus().toggleBold().run(), active: editor.value.isActive('bold'), title: 'Bold' },
    { icon: 'i-heroicons-italic', action: () => editor.value?.chain().focus().toggleItalic().run(), active: editor.value.isActive('italic'), title: 'Italic' },
    { icon: 'i-heroicons-underline', action: () => editor.value?.chain().focus().toggleUnderline().run(), active: editor.value.isActive('underline'), title: 'Underline' },
    { icon: 'i-heroicons-strikethrough', action: () => editor.value?.chain().focus().toggleStrike().run(), active: editor.value.isActive('strike'), title: 'Strike' },
    { divider: true },
    { icon: 'i-heroicons-h1', action: () => editor.value?.chain().focus().toggleHeading({ level: 2 }).run(), active: editor.value.isActive('heading', { level: 2 }), title: 'Heading' },
    { icon: 'i-heroicons-list-bullet', action: () => editor.value?.chain().focus().toggleBulletList().run(), active: editor.value.isActive('bulletList'), title: 'Bullet List' },
    { icon: 'i-heroicons-numbered-list', action: () => editor.value?.chain().focus().toggleOrderedList().run(), active: editor.value.isActive('orderedList'), title: 'Ordered List' },
    { divider: true },
    { icon: 'i-heroicons-bars-3-bottom-left', action: () => editor.value?.chain().focus().setTextAlign('left').run(), active: editor.value.isActive({ textAlign: 'left' }), title: 'Left' },
    { icon: 'i-heroicons-bars-3', action: () => editor.value?.chain().focus().setTextAlign('center').run(), active: editor.value.isActive({ textAlign: 'center' }), title: 'Center' },
    { icon: 'i-heroicons-bars-3-bottom-right', action: () => editor.value?.chain().focus().setTextAlign('right').run(), active: editor.value.isActive({ textAlign: 'right' }), title: 'Right' },
  ]
})

const charCount = computed(() => editor.value?.storage.characterCount.characters() || 0)
const wordCount = computed(() => editor.value?.storage.characterCount.words() || 0)

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

onBeforeUnmount(() => {
  if (saveTimeout) clearTimeout(saveTimeout)
  editor.value?.destroy()
})
</script>

<template>
  <div class="flex flex-col h-full">
    <!-- Toolbar -->
    <div class="flex items-center flex-wrap gap-0.5 px-3 py-1.5 border-b border-gray-100 bg-gray-50/50">
      <template v-for="(item, idx) in toolbarItems" :key="idx">
        <div v-if="(item as any).divider" class="w-px h-5 bg-gray-200 mx-1" />
        <UButton
            v-else
            :icon="item.icon"
            size="xs"
            :color="item.active ? 'primary' : 'neutral'"
            :variant="item.active ? 'soft' : 'ghost'"
            :title="item.title"
            @click="item.action"
        />
      </template>
    </div>

    <!-- Editor -->
    <div class="flex-1 overflow-auto">
      <EditorContent :editor="editor" />
    </div>

    <!-- Footer with stats and auto-save indicator -->
    <div class="flex items-center justify-between px-3 py-1.5 border-t border-gray-100 text-xs text-gray-400">
      <span>{{ wordCount }} words · {{ charCount }} chars</span>
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

<style>
/* TipTap editor styles */
.tiptap p.is-editor-empty:first-child::before {
  content: attr(data-placeholder);
  float: left;
  color: #adb5bd;
  pointer-events: none;
  height: 0;
}
.tiptap:focus {
  outline: none;
}
.tiptap h1, .tiptap h2, .tiptap h3 {
  font-weight: 700;
  line-height: 1.3;
}
.tiptap h2 { font-size: 1.25rem; margin: 0.75rem 0 0.5rem; }
.tiptap ul, .tiptap ol { padding-left: 1.5rem; }
.tiptap ul { list-style-type: disc; }
.tiptap ol { list-style-type: decimal; }
.tiptap li { margin: 0.15rem 0; }
.tiptap mark { background-color: #fef08a; border-radius: 2px; padding: 0 2px; }
.tiptap blockquote {
  border-left: 3px solid #e5e7eb;
  padding-left: 1rem;
  color: #6b7280;
  margin: 0.5rem 0;
}
</style>