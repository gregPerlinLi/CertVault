<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import type { MenuItem } from "primevue/menuitem";

// Reactive
const router = useRouter();
const { role } = useUserStore();

// Computed
const menuModel = computed((): MenuItem[] =>
  [
    {
      label: "Dashboard",
      icon: "pi pi-gauge",
      command: (): void => {
        router.push("/dashboard");
      }
    },
    {
      label: "My Profile",
      icon: "pi pi-user",
      command: (): void => {
        router.push("/dashboard/profile");
      }
    },
    {
      label: "Users",
      icon: "pi pi-users",
      only: ["Admin", "Superadmin"],
      command: (): void => {
        router.push("/dashboard/users");
      }
    },
    {
      label: "Certificates",
      icon: "pi pi-verified",
      command: (): void => {
        router.push("/dashboard/certificates");
      }
    },
    {
      label: "Settings",
      icon: "pi pi-cog",
      only: ["Superadmin"],
      command: (): void => {
        router.push("/dashboard/settings");
      }
    }
  ].filter(
    (itm: MenuItem & { only?: string[] }): boolean =>
      itm?.only?.includes(role.value) ?? true
  )
);
</script>

<template>
  <aside>
    <div class="flex flex-col gap-4 p-4 sticky top-[65px]">
      <PanelMenu :model="menuModel" />
      <hr class="border-neutral-200 border-t-2 dark:border-neutral-500" />
      <p class="leading-none select-none text-center text-neutral-400 text-sm">
        {{ role }} View
      </p>
    </div>
  </aside>
</template>
