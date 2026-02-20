<script setup lang="ts">
import * as z from 'zod'
import type { FormSubmitEvent } from '@nuxt/ui'

const authStore = useAuthStore()
const toast = useToast()

const schema = z.object({
  name: z.string().min(1, 'Name is required'),
  email: z.string().email('Invalid email'),
  password: z.string().min(8, 'Must be at least 8 characters'),
})

type Schema = z.output<typeof schema>

const state = reactive<Partial<Schema>>({
  name: undefined,
  email: undefined,
  password: undefined,
})

const onSubmit = async (event: FormSubmitEvent<Schema>) => {
  try {
    await authStore.register(event.data.name, event.data.email, event.data.password)
    toast.add({
      title: 'User Registered',
      description: 'Thanks for registering!',
      color: 'success',
    })
  } catch (error: any) {
    toast.add({
      title: 'Error Registering',
      description: error?.data?.message || 'There was an issue registering',
      color: 'error',
    })
  }
}
</script>

<template>
  <UCard class="max-w-md m-auto my-10">
    <template #header>
      <h1 class="text-2xl text-center">Register</h1>
    </template>

    <UForm :schema="schema" :state="state" class="space-y-4" @submit="onSubmit">
      <UFormField label="Name" name="name">
        <UInput v-model="state.name" class="w-full" />
      </UFormField>

      <UFormField label="Email" name="email">
        <UInput v-model="state.email" class="w-full" />
      </UFormField>

      <UFormField label="Password" name="password">
        <UInput v-model="state.password" type="password" class="w-full" />
      </UFormField>

      <UButton type="submit" block> Register </UButton>
    </UForm>

    <div class="text-center mt-4 text-sm text-gray-600">
      Already have an account?
      <NuxtLink to="/login" class="text-primary hover:underline font-medium">
        Login here
      </NuxtLink>
    </div>
  </UCard>
</template>