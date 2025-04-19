<script setup lang="ts">
import { useUserStore } from "@/stores/user";

/* Stores */
const user = useUserStore();
</script>

<template>
  <!-- Headers -->
  <Breadcrumb
    class="mb-4"
    :home="{ icon: 'pi pi-home' }"
    :model="[
      {
        label: 'Dashboard',
        icon: 'pi pi-gauge'
      }
    ]">
    <template #item="{ item }">
      <div class="flex gap-2 items-center">
        <span :class="item.icon"></span>
        <span>{{ item.label }}</span>
      </div>
    </template>
  </Breadcrumb>

  <!-- Main -->
  <h1 class="font-bold text-xl">
    Welcome to use CertVault,
    <span :class="user.getRoleClass.value()">{{ user.displayName }}</span>
  </h1>
</template>
