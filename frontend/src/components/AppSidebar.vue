<script setup lang="ts">
import { useMainStore } from "@/stores";
import type { MenuItem } from "primevue/menuitem";

// Reactive
const router = useRouter();
const { role } = useMainStore();

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
      only: ["admin", "super"],
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
      only: ["super"],
      command: (): void => {
        router.push("/dashboard/settings");
      }
    }
  ].filter(
    (itm: MenuItem & { only?: string[] }): boolean =>
      itm?.only?.includes(role.value) ?? true
  )
);
const roleName = computed((): string => {
  switch (role.value) {
    case "super":
      return "Superadmin View";
    case "admin":
      return "Admin View";
    case "user":
      return "User View";
  }
});
</script>

<template>
  <aside>
    <div class="flex flex-col gap-4 p-4 sticky top-[65px]">
      <PanelMenu :model="menuModel" />
      <hr class="border-gray-500 border-t-2" />
      <p class="leading-none select-none text-center text-gray-500 text-sm">
        {{ roleName }}
      </p>
    </div>
  </aside>
</template>
