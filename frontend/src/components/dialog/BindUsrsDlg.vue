<script setup lang="ts">
import type { CaInfoDTO, UserProfileDTO } from "@api/types";
import { bindCaToUsrs, getAllCaNotBindedUsrs } from "@api/admin/cert/binding";

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
const props = defineProps<{ ca: CaInfoDTO | null }>();

// Emits
const emits = defineEmits<{ success: [] }>();

/* Services */
const { success, info, error, remove } = useNotify();

const refUsrTable = useTemplateRef("usr-table");

/* Reactives */
const busyBind = ref(false);

const usrTable = reactive({
  selection: [] as UserProfileDTO[],
  loading: false
});

/* Actions */
const tryBind = async () => {
  busyBind.value = true;
  const msg = info("Binding");

  try {
    await bindCaToUsrs({
      caUuid: props.ca!.uuid,
      usernames: usrTable.selection.map(({ username }) => username)
    });

    success("Successfully binded");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Bind");
  }

  remove(msg);
  busyBind.value = false;
};

/* Watches */
watch(visible, async (newValue) => {
  if (newValue) {
    await nextTick();
    refUsrTable.value?.reload();
    refUsrTable.value?.refresh();
  } else {
    refUsrTable.value?.cancel();
    busyBind.value = false;
    refUsrTable.value?.resetStates();
  }
});
</script>

<template>
  <Dialog v-model:visible="visible" header="Bind Users" :closable="false" modal>
    <!-- User table -->
    <UsrTbl
      v-model:selection="usrTable.selection"
      v-model:loading="usrTable.loading"
      ref="usr-table"
      :refresh-fn="
        (page, limit, keyword, abort) =>
          getAllCaNotBindedUsrs({
            uuid: ca!.uuid,
            page,
            limit,
            keyword,
            abort
          })
      "
      selectable />

    <!-- Buttons -->
    <div class="flex justify-between mt-4">
      <Button
        label="Refresh"
        severity="info"
        size="small"
        :disabled="busyBind"
        :loading="usrTable.loading"
        @click="refUsrTable?.refresh()"></Button>
      <div class="flex gap-2">
        <Button
          label="Cancel"
          severity="secondary"
          size="small"
          :disabled="busyBind"
          @click="visible = false"></Button>
        <Button
          label="Bind"
          size="small"
          :disabled="usrTable.selection.length === 0"
          :loading="busyBind"
          @click="tryBind"></Button>
      </div>
    </div>
  </Dialog>
</template>
