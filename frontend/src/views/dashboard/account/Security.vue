<script setup lang="ts">
import { forceLogoutUsr, getUsrLoginRecs } from "@api/user/user";

/* Services */
const router = useRouter();
const { getSignal } = useAsyncGuard();

/* Stores */
const user = useUserStore();

/* Reactives */
const refOnlineSessTbl = useTemplateRef("online-sess-tbl");
const refOfflineSessTbl = useTemplateRef("offline-sess-tbl");

/* Hooks */
onMounted(() => {
  refOnlineSessTbl.value?.refresh();
  refOfflineSessTbl.value?.refresh();
});
</script>

<template>
  <!-- Header -->
  <Breadcrumb
    :home="{ icon: 'pi pi-home' }"
    :model="[
      {
        label: 'My Account',
        icon: 'pi pi-user'
      },
      {
        label: 'Security',
        icon: 'pi pi-shield'
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
  <h2 class="font-bold my-4 text-lg">Online Sessions</h2>
  <OnlineSessTbl
    ref="online-sess-tbl"
    :fetch-fn="
      () =>
        getUsrLoginRecs({
          page: 1,
          limit: 5,
          status: 1,
          abort: { signal: getSignal() }
        })
    "
    :sign-out-all-fn="
      async () => {
        await forceLogoutUsr();
        user.clear();
        router.push('/');
      }
    " />

  <h2 class="font-bold my-4 text-lg">Last 20 Offline Sessions</h2>
  <OfflineSessTbl
    ref="offline-sess-tbl"
    :fetch-fn="
      () =>
        getUsrLoginRecs({
          page: 1,
          limit: 20,
          status: 0,
          orderBy: 'loginTime',
          isAsc: false,
          abort: { signal: getSignal() }
        })
    " />
</template>
