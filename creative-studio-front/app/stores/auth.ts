import { defineStore } from 'pinia'
import {useApi} from "~/composables/api";

interface User {
    userId: string
    name: string
    email: string
}

export const useAuthStore = defineStore('auth', () => {
    const user = ref<User | null>(null)
    const token = ref<string | null>(null)
    const isLoggedIn = computed(() => !!token.value)

    const api = useApi()
    const router = useRouter()

    // Load from localStorage on init
    const init = () => {
        if (import.meta.client) {
            const savedToken = localStorage.getItem('token')
            const savedUser = localStorage.getItem('user')
            if (savedToken && savedUser) {
                token.value = savedToken
                user.value = JSON.parse(savedUser)
            }
        }
    }

    const register = async (name: string, email: string, password: string) => {
        const response: any = await api.post('/auth/register', { name, email, password })
        token.value = response.token
        user.value = {
            userId: response.userId,
            name: response.name,
            email: response.email,
        }
        if (import.meta.client) {
            localStorage.setItem('token', response.token)
            localStorage.setItem('user', JSON.stringify(user.value))
        }
        router.push('/')
    }

    const login = async (email: string, password: string) => {
        const response: any = await api.post('/auth/login', { email, password })
        token.value = response.token
        user.value = {
            userId: response.userId,
            name: response.name,
            email: response.email,
        }
        if (import.meta.client) {
            localStorage.setItem('token', response.token)
            localStorage.setItem('user', JSON.stringify(user.value))
        }
        router.push('/')
    }

    const logout = () => {
        token.value = null
        user.value = null
        if (import.meta.client) {
            localStorage.removeItem('token')
            localStorage.removeItem('user')
        }
        router.push('/login')
    }

    return {
        user,
        token,
        isLoggedIn,
        init,
        register,
        login,
        logout,
    }
})