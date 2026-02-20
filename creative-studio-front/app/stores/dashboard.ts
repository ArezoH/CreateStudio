import { defineStore } from 'pinia'

interface Widget {
    id: string
    type: string
    name: string
    x: number
    y: number
    width: number
    height: number
    zIndex: number
    data: Record<string, any>
    createdAt: string
    updatedAt: string
}

interface Dashboard {
    id: string
    name: string
    gridSize: number
    widgets: Widget[]
    createdAt: string
}

export const useDashboardStore = defineStore('dashboard', () => {
    const api = useApi()

    const dashboards = ref<Dashboard[]>([])
    const currentDashboard = ref<Dashboard | null>(null)
    const widgets = ref<Widget[]>([])
    const isLoading = ref(false)
    const selectedWidget = ref<string | null>(null)

    const widgetTypes = [
        { id: 'content-editor', name: 'Content Editor', icon: 'i-heroicons-pencil', color: 'from-blue-500 to-purple-600', defaultSize: { width: 400, height: 400 } },
        { id: 'todo-list', name: 'Todo List', icon: 'i-heroicons-check-circle', color: 'from-orange-500 to-red-600', defaultSize: { width: 400, height: 400 } },
        { id: 'notes-widget', name: 'Quick Notes', icon: 'i-heroicons-document-text', color: 'from-green-500 to-teal-600', defaultSize: { width: 400, height: 400 } },
    ]

    // --- Dashboard CRUD ---

    const loadDashboards = async () => {
        isLoading.value = true
        try {
            dashboards.value = await api.get('/dashboards') as Dashboard[]
        } catch (e) {
            console.error('Failed to load dashboards:', e)
        } finally {
            isLoading.value = false
        }
    }

    const createDashboard = async (name: string) => {
        try {
            const dashboard = await api.post('/dashboards', { name }) as Dashboard
            dashboards.value.push(dashboard)
            return dashboard
        } catch (e) {
            console.error('Failed to create dashboard:', e)
        }
    }

    const loadDashboard = async (id: string) => {
        isLoading.value = true
        try {
            currentDashboard.value = await api.get(`/dashboards/${id}`) as Dashboard
            widgets.value = currentDashboard.value.widgets || []
        } catch (e) {
            console.error('Failed to load dashboard:', e)
        } finally {
            isLoading.value = false
        }
    }
    const updateDashboard = async (id: string, name: string) => {
        try {
            const updated = await api.put(`/dashboards/${id}`, { name }) as Dashboard
            const index = dashboards.value.findIndex(d => d.id === id)
            if (index !== -1) {
                dashboards.value[index] = updated
            }
            if (currentDashboard.value?.id === id) {
                currentDashboard.value = updated
            }
            return updated
        } catch (e) {updateDashboard
            console.error('Failed to update dashboard:', e)
            throw e
        }
    }

    const deleteDashboard = async (id: string) => {
        try {
            await api.del(`/dashboards/${id}`)
            dashboards.value = dashboards.value.filter(d => d.id !== id)
            if (currentDashboard.value?.id === id) {
                currentDashboard.value = null
                widgets.value = []
            }
        } catch (e) {
            console.error('Failed to delete dashboard:', e)
        }
    }

    // --- Widget CRUD ---

    const addWidget = async (type: string) => {
        if (!currentDashboard.value) return
        const widgetType = widgetTypes.find(wt => wt.id === type)
        if (!widgetType) return

        try {
            const widget = await api.post(`/dashboards/${currentDashboard.value.id}/widgets`, {
                type,
                name: widgetType.name,
                x: 0,
                y: 0,
                width: widgetType.defaultSize.width,
                height: widgetType.defaultSize.height,
                data: {},
            }) as Widget
            widgets.value.push(widget)
            return widget
        } catch (e) {
            console.error('Failed to create widget:', e)
        }
    }

    const updateWidget = async (widgetId: string, updates: Partial<Widget>) => {
        if (!currentDashboard.value) return

        // Optimistic update
        const index = widgets.value.findIndex(w => w.id === widgetId)
        if (index !== -1) {
            widgets.value[index] = { ...widgets.value[index], ...updates }
        }

        try {
            const widget = widgets.value[index]
            await api.put(`/dashboards/${currentDashboard.value.id}/widgets/${widgetId}`, {
                type: widget.type,
                name: widget.name,
                x: widget.x,
                y: widget.y,
                width: widget.width,
                height: widget.height,
                data: widget.data,
            })
        } catch (e) {
            console.error('Failed to update widget:', e)
        }
    }

    const updateWidgetData = async (widgetId: string, data: Record<string, any>) => {
        if (!currentDashboard.value) return

        // Optimistic update
        const index = widgets.value.findIndex(w => w.id === widgetId)
        if (index !== -1) {
            widgets.value[index].data = data
        }

        try {
            await api.patch(`/dashboards/${currentDashboard.value.id}/widgets/${widgetId}/data`, data)
        } catch (e) {
            console.error('Failed to update widget data:', e)
        }
    }

    const removeWidget = async (widgetId: string) => {
        if (!currentDashboard.value) return

        widgets.value = widgets.value.filter(w => w.id !== widgetId)

        try {
            await api.del(`/dashboards/${currentDashboard.value.id}/widgets/${widgetId}`)
        } catch (e) {
            console.error('Failed to delete widget:', e)
            // Reload on error
            if (currentDashboard.value) {
                await loadDashboard(currentDashboard.value.id)
            }
        }
    }

    return {
        dashboards,
        currentDashboard,
        widgets,
        isLoading,
        selectedWidget,
        widgetTypes,
        loadDashboards,
        createDashboard,
        loadDashboard,
        updateDashboard,
        deleteDashboard,
        addWidget,
        updateWidget,
        updateWidgetData,
        removeWidget,
    }
})