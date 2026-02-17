export default defineNuxtConfig({
    compatibilityDate: '2025-07-15',
    devtools: { enabled: true },
    modules: [
        '@nuxt/ui',
        '@pinia/nuxt',
    ],
    runtimeConfig: {
        public: {
            apiBase: 'http://localhost:8080/api'
        }
    },
    css: ['~/assets/css/main.css'],
})