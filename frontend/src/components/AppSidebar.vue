<script setup lang="ts">
import type { MenuItem } from "primevue/menuitem";
import { useRole } from "@/utils/composable";

// Services
const router = useRouter();
const { role, aboveUser } = useRole();

// Computed
const menuModel = computed(() =>
  (
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
        show: aboveUser.value,
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
      }
    ] satisfies MenuItem[]
  ).filter((itm: MenuItem & { only?: string[] }): boolean => itm?.show ?? true)
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
