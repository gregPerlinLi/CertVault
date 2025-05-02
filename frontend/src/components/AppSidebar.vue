<script setup lang="ts">
import type { MenuItem } from "primevue/menuitem";

/* Services */
const router = useRouter();

/* Stores */
const { displayRole, isAdmin, isSuperadmin } = useUserStore();

/* Computed */
const menuModel = computed(() =>
  (
    [
      {
        label: "Dashboard",
        icon: "pi pi-gauge",
        command: () => router.push("/dashboard")
      },
      {
        label: "My Account",
        icon: "pi pi-user",
        items: [
          {
            label: "Profile",
            icon: "pi pi-user-edit",
            command: () => router.push("/dashboard/account/profile")
          },
          {
            label: "Security",
            icon: "pi pi-shield",
            command: () => router.push("/dashboard/account/security")
          }
        ]
      },
      {
        label: "Users",
        icon: "pi pi-users",
        show: isAdmin.value || isSuperadmin.value,
        command: () => router.push("/dashboard/users")
      },
      {
        label: "Certificates",
        icon: "pi pi-verified",
        items: [
          {
            label: "CA Certificates",
            icon: "pi pi-building-columns",
            command: () => router.push("/dashboard/certificates/ca")
          },
          {
            label: "SSL Certificates",
            icon: "pi pi-receipt",
            command: () => router.push("/dashboard/certificates/ssl")
          }
        ]
      },
      {
        label: "CA Binding",
        icon: "pi pi-link",
        show: isAdmin.value || isSuperadmin.value,
        command: () => router.push("/dashboard/binding")
      }
    ] satisfies MenuItem[]
  ).filter((itm: MenuItem & { only?: string[] }): boolean => itm?.show ?? true)
);
</script>

<template>
  <aside>
    <div class="flex flex-col gap-4 p-4 sticky top-[73px]">
      <PanelMenu
        class="w-60"
        :model="menuModel"
        :pt="{ submenuIcon: { class: 'hidden!' } }"
        multiple />
      <hr class="border-neutral-200 border-t-2 dark:border-neutral-500" />
      <p class="leading-none select-none text-center text-neutral-400 text-sm">
        {{ displayRole }} View
      </p>
      <p class="leading-none select-none text-center text-neutral-400 text-sm">
        <a
          class="hover:text-blue-500"
          href="/swagger-ui/index.html"
          rel="noopener noreferer"
          target="_blank"
          >API Doc</a
        >
      </p>
    </div>
  </aside>
</template>
