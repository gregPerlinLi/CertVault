<script setup lang="ts">
import type { UserProfileDTO } from "@/api/types";
import { forceLogoutUsr, getUsrAllLoginRecs } from "@/api/superadmin/user";

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
defineProps<{ user: UserProfileDTO | null }>();

/* Services */
const { reset, cancel } = useAsyncGuard();

/* Reactive */
const refOnlineSessTbl = useTemplateRef("online-sess-tbl");
const refOfflineSessTbl = useTemplateRef("offline-sess-tbl");

const invalid = ref(false);
const busy = ref(false);

/* Watches */
watch(visible, async (newValue) => {
  if (newValue) {
    reset();
    await nextTick();
    refOnlineSessTbl.value?.reset();
    refOfflineSessTbl.value?.reset();
    refOnlineSessTbl.value?.refresh();
    refOfflineSessTbl.value?.refresh();
  } else {
    cancel();
    refOnlineSessTbl.value?.cancel();
    refOfflineSessTbl.value?.cancel();

    invalid.value = false;
    busy.value = false;
    refOnlineSessTbl.value?.resetStates();
    refOfflineSessTbl.value?.resetStates();
  }
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    :header="`Manage Sessions for ${user?.displayName}`"
    modal>
    <h2 class="font-bold my-4 text-lg">Online Sessions</h2>
    <OnlineSessTbl
      ref="online-sess-tbl"
      :fetch-fn="
        (abort) =>
          getUsrAllLoginRecs({
            page: 1,
            limit: 5,
            status: 1,
            username: user!.username,
            orderBy: 'loginTime',
            isAsc: false,
            abort
          })
      "
      :sign-out-all-fn="
        async (abort) => {
          await forceLogoutUsr({ username: user!.username, abort });
        }
      " />

    <h2 class="font-bold my-4 text-lg">Last 20 Offline Sessions</h2>
    <OfflineSessTbl
      ref="offline-sess-tbl"
      :fetch-fn="
        (abort) =>
          getUsrAllLoginRecs({
            page: 1,
            limit: 20,
            status: 0,
            username: user!.username,
            orderBy: 'loginTime',
            isAsc: false,
            abort
          })
      " />
  </Dialog>
</template>
