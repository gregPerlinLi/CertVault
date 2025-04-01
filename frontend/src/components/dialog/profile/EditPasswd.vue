<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { useNotify } from "@/utils/composable";

// Models
const visible = defineModel<boolean>("visible");
const newPassword = defineModel<string>("new-password");

// Stores
const { syncToRemote } = useUserStore();

// Services
const { toast, info, error } = useNotify();

// Reactive
const invalid = ref(false);
const busy = ref(false);

const oldPassword = ref("");

// Actions
const submit = async () => {
  // Clear error flag
  invalid.value = false;

  // Validate
  if (oldPassword.value.length === 0) {
    invalid.value = true;
    error("Validation Error", "Old password is required");
    return;
  }

  // Try update
  busy.value = true;
  info("Info", "Updating");

  const err = await syncToRemote(
    { oldPassword: oldPassword.value, newPassword: newPassword.value },
    toast
  );
  if (err === null) {
    newPassword.value = "";
    visible.value = false;
  }
  busy.value = false;
};
</script>

<template>
  <Dialog
    v-model:visible="visible"
    header="Confirm Old Password"
    :closable="false"
    modal>
    <form @submit.prevent="submit">
      <InputText
        v-model="oldPassword"
        class="mb-4 w-full"
        type="password"
        :disabled="busy"
        :invalid="invalid" />
      <div class="flex justify-end gap-2">
        <Button
          label="Cancel"
          severity="secondary"
          size="small"
          type="button"
          :disabled="busy"
          @click="
            () => {
              newPassword = '';
              visible = false;
            }
          "></Button>
        <Button
          label="Confirm"
          size="small"
          type="submit"
          :disabled="busy"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
