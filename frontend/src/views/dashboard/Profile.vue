<script setup lang="ts">
import { useUserStore } from "@/stores/user";

useTitle("My Profile - CertVault");

// Stores
const { username, displayName, email, role } = useUserStore();
</script>

<template>
  <h1 class="font-bold text-2xl">
    <i class="mr-2 pi pi-user text-xl"></i>My profile
  </h1>
  <hr class="border-2 border-neutral-200 dark:border-neutral-500 my-2" />
  <section>
    <h2>Username</h2>
    <p>{{ username }}</p>
  </section>
  <section>
    <h2>Display Name</h2>
    <p>
      {{ displayName }}
      <Button
        icon="pi pi-pen-to-square"
        severity="help"
        size="small"
        variant="text"></Button>
    </p>
  </section>
  <section>
    <h2>Email</h2>
    <p>
      {{ email }}
      <Button
        icon="pi pi-pen-to-square"
        severity="help"
        size="small"
        variant="text"></Button>
    </p>
  </section>
  <section>
    <h2>Role</h2>
    <p>{{ role }}</p>
  </section>
  <section>
    <h2>New Password</h2>
    <form class="flex gap-2 items-center mt-1" @submit.prevent="() => {}">
      <Password size="small" />
      <Button
        icon="pi pi-check"
        severity="help"
        size="small"
        type="submit"
        variant="text"></Button>
    </form>
  </section>
</template>

<style>
@import "tailwindcss";

section {
  @apply my-4;

  h2 {
    @apply font-bold text-xl;
  }
}
</style>
