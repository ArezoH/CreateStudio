<script setup lang="ts">
const props = defineProps<{
  widgetId: string
  data: Record<string, any>
}>()

const dashboardStore = useDashboardStore()

interface Todo {
  id: string
  text: string
  completed: boolean
  createdAt: string
}

const todos = ref<Todo[]>(props.data?.todos || [])
const newTodoText = ref('')
const filter = ref<'all' | 'active' | 'completed'>('all')

// Save with debounce
let saveTimeout: ReturnType<typeof setTimeout> | null = null

const saveTodos = () => {
  if (saveTimeout) clearTimeout(saveTimeout)
  saveTimeout = setTimeout(() => {
    dashboardStore.updateWidgetData(props.widgetId, {
      ...props.data,
      todos: todos.value,
    })
  }, 300)
}

const addTodo = () => {
  const text = newTodoText.value.trim()
  if (!text) return

  todos.value.unshift({
    id: `todo-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
    text,
    completed: false,
    createdAt: new Date().toISOString(),
  })
  newTodoText.value = ''
  saveTodos()
}

const toggleTodo = (id: string) => {
  const todo = todos.value.find(t => t.id === id)
  if (todo) {
    todo.completed = !todo.completed
    saveTodos()
  }
}

const deleteTodo = (id: string) => {
  todos.value = todos.value.filter(t => t.id !== id)
  saveTodos()
}

const clearCompleted = () => {
  todos.value = todos.value.filter(t => !t.completed)
  saveTodos()
}

// Computed
const filteredTodos = computed(() => {
  if (filter.value === 'active') return todos.value.filter(t => !t.completed)
  if (filter.value === 'completed') return todos.value.filter(t => t.completed)
  return todos.value
})

const activeCount = computed(() => todos.value.filter(t => !t.completed).length)
const completedCount = computed(() => todos.value.filter(t => t.completed).length)
const progress = computed(() => {
  if (todos.value.length === 0) return 0
  return Math.round((completedCount.value / todos.value.length) * 100)
})

onBeforeUnmount(() => {
  if (saveTimeout) clearTimeout(saveTimeout)
})
</script>

<template>
  <div class="flex flex-col h-full">
    <!-- Add todo input -->
    <form @submit.prevent="addTodo" class="flex gap-2 px-4 pt-3 pb-2">
      <UInput
          v-model="newTodoText"
          placeholder="Add a new task..."
          class="flex-1"
          size="sm"
          maxlength="120"
          @keydown.enter.prevent="addTodo"
      />
      <UButton
          type="submit"
          icon="i-heroicons-plus"
          size="sm"
          color="primary"
          :disabled="!newTodoText.trim()"
      />
    </form>

    <!-- Progress bar -->
    <div v-if="todos.length > 0" class="px-4 pb-2">
      <div class="flex items-center justify-between text-xs text-gray-500 mb-1">
        <span>{{ completedCount }}/{{ todos.length }} done</span>
        <span>{{ progress }}%</span>
      </div>
      <div class="w-full h-1.5 bg-gray-100 rounded-full overflow-hidden">
        <div
            class="h-full bg-gradient-to-r from-pink-500 to-purple-500 rounded-full transition-all duration-500 ease-out"
            :style="{ width: `${progress}%` }"
        />
      </div>
    </div>

    <!-- Filter tabs -->
    <div v-if="todos.length > 0" class="flex items-center gap-1 px-4 pb-2">
      <UButton
          v-for="f in (['all', 'active', 'completed'] as const)"
          :key="f"
          size="xs"
          :color="filter === f ? 'primary' : 'neutral'"
          :variant="filter === f ? 'soft' : 'ghost'"
          @click="filter = f"
      >
        {{ f === 'all' ? `All (${todos.length})` : f === 'active' ? `Active (${activeCount})` : `Done (${completedCount})` }}
      </UButton>
      <div class="flex-1" />
      <UButton
          v-if="completedCount > 0"
          size="xs"
          color="neutral"
          variant="ghost"
          icon="i-heroicons-trash"
          @click="clearCompleted"
          title="Clear completed"
      />
    </div>

    <!-- Todo List -->
    <div class="flex-1 overflow-auto px-4 pb-3 space-y-1">
      <div v-if="filteredTodos.length === 0" class="flex flex-col items-center justify-center py-8 text-gray-400">
        <UIcon name="i-heroicons-clipboard-document-check" class="w-10 h-10 mb-2" />
        <p class="text-sm">{{ todos.length === 0 ? 'No tasks yet' : 'No tasks match this filter' }}</p>
      </div>

      <TransitionGroup name="todo">
        <div
            v-for="todo in filteredTodos"
            :key="todo.id"
            class="group flex items-center gap-2 py-2 px-2 rounded-lg hover:bg-gray-50 transition-colors"
        >
          <input
              type="checkbox"
              :checked="todo.completed"
              class="h-4 w-4 rounded border-gray-300 text-pink-500 focus:ring-pink-400 cursor-pointer flex-shrink-0"
              @click="toggleTodo(todo.id)"
          />
          <span
              class="flex-1 text-sm transition-all duration-200 min-w-0 truncate"
              :class="todo.completed ? 'line-through text-gray-400' : 'text-gray-800'"
          >
            {{ todo.text }}
          </span>
          <UButton
              icon="i-heroicons-x-mark"
              size="xs"
              color="error"
              variant="ghost"
              class="opacity-0 group-hover:opacity-100 transition-opacity flex-shrink-0"
              @click="deleteTodo(todo.id)"
          />
        </div>
      </TransitionGroup>
    </div>
  </div>
</template>

<style scoped>
.todo-enter-active,
.todo-leave-active {
  transition: all 0.25s ease;
}
.todo-enter-from {
  opacity: 0;
  transform: translateY(-8px);
}
.todo-leave-to {
  opacity: 0;
  transform: translateX(40px);
}
</style>