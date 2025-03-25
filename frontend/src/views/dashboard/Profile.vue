<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { useToast } from "primevue/usetoast";

useTitle("My Profile - CertVault");

// Stores
const { username, displayName, email, role } = useUserStore();

// Reactive
const toast = useToast();

const showEditDisplayName = ref(false);
const showEditEmail = ref(false);
const showEditPassword = ref(false);

const newPassword = ref("");

// Action
const onSubmitNewPassword = () => {
  if (newPassword.value.length === 0) {
    toast.add({
      severity: "error",
      summary: "Validation Error",
      detail: "New password is required",
      life: 5000
    });
    return;
  }

  showEditPassword.value = true;
};
</script>

<template>
  <!-- Header -->
  <h1 class="font-bold text-2xl">
    <i class="mr-2 pi pi-user text-xl"></i>My profile
  </h1>
  <hr class="border-2 border-neutral-200 dark:border-neutral-500 my-2" />

  <!-- Main -->
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
        type="button"
        variant="text"
        @click="showEditDisplayName = true"></Button>
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
        type="button"
        variant="text"
        @click="showEditEmail = true"></Button>
    </p>
  </section>
  <section>
    <h2>Role</h2>
    <p>{{ role }}</p>
  </section>
  <section>
    <h2>New Password</h2>
    <form
      class="flex gap-2 items-center mt-1"
      @submit.prevent="onSubmitNewPassword">
      <Password v-model:model-value="newPassword" size="small" />
      <Button
        icon="pi pi-check"
        severity="help"
        size="small"
        type="submit"
        variant="text"></Button>
    </form>
  </section>

  <!-- Dialogs -->
  <EditDisplayName
    v-model:visible="showEditDisplayName"
    :value="displayName ?? ''" />
  <EditEmail v-model:visible="showEditEmail" :value="email ?? ''" />
  <EditPassword
    v-model:visible="showEditPassword"
    v-model:new-password="newPassword" />
</template>

<style scoped>
@import "tailwindcss";

section {
  @apply my-4;

  h2 {
    @apply font-bold text-xl;
  }
}
</style>
