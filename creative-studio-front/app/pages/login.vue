<script setup lang="ts">
import * as z from 'zod'
import type { FormSubmitEvent } from '@nuxt/ui'

const authStore = useAuthStore()
const toast = useToast()

const schema = z.object({
  email: z.string().email('Invalid email'),
  password: z.string().min(8, 'Must be at least 8 characters'),
})

type Schema = z.output<typeof schema>

const state = reactive<Partial<Schema>>({
  email: undefined,
  password: undefined,
})

const onSubmit = async (event: FormSubmitEvent<Schema>) => {
  try {
    await authStore.login(event.data.email, event.data.password)
    toast.add({
      title: 'Logged In Successfully!',
      description: 'Welcome!',
      color: 'primary',
    })
  } catch (error: any) {
    toast.add({
      title: 'Error Logging In',
      description: error?.data?.message || 'Invalid email or password',
      color: 'error',
    })
  }
}
</script>

<template>
  <UCard class="max-w-md m-auto my-10">
    <template #header>
      <h1 class="text-2xl text-center">Login</h1>
    </template>

    <UForm :schema="schema" :state="state" class="space-y-4" @submit="onSubmit">
      <UFormField label="Email" name="email">
        <UInput v-model="state.email" class="w-full" />
      </UFormField>

      <UFormField label="Password" name="password">
        <UInput v-model="state.password" type="password" class="w-full" />
      </UFormField>

      <UButton type="submit" block> Login </UButton>
    </UForm>

    <div class="text-center mt-4 text-sm text-gray-600">
      Don't have an account?
      <NuxtLink to="/register" class="text-primary hover:underline font-medium">
        Sign up here
      </NuxtLink>
    </div>
  </UCard>
</template>