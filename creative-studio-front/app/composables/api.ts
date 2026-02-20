export const useApi = () => {
    const getToken = () => {
        if (import.meta.client) {
            return localStorage.getItem('token')
        }
        return null
    }

    const request = async (url: string, options: any = {}) => {
        const token = getToken()

        const headers: any = {
            'Content-Type': 'application/json',
            ...options.headers,
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`
        }

        const response = await $fetch(`/api${url}`, {
            ...options,
            headers,
        })

        return response
    }

    return {
        get: (url: string) => request(url, { method: 'GET' }),
        post: (url: string, body: any) => request(url, { method: 'POST', body }),
        put: (url: string, body: any) => request(url, { method: 'PUT', body }),
        patch: (url: string, body: any) => request(url, { method: 'PATCH', body }),
        del: (url: string) => request(url, { method: 'DELETE' }),
    }
}