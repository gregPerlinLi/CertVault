<script setup lang="ts">
import { updateProfile } from "@api/user/user";

/* Models */
const visible = defineModel<boolean>("visible");
const newPassword = defineModel<string>("new-password");

/* Services */
const { success, info, warn, error, remove } = useNotify();

/* Reactive */
const invalid = ref(false);
const busy = ref(false);

const oldPassword = ref("");

/* Actions */
const submit = async () => {
  // Clear error flag
  invalid.value = false;

  // Validate
  if (oldPassword.value.length === 0) {
    invalid.value = true;
    warn("Old password is required");
    return;
  }

  // Try update
  busy.value = true;
  const msg = info("Updating");

  try {
    await updateProfile({
      oldPassword: oldPassword.value,
      newPassword: newPassword.value
    });
    visible.value = false;
    success("Successfully updated profile");
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Update Profile");
  }

  remove(msg);
  busy.value = false;
};

/* Watches */
watch(visible, (newValue) => {
  if (!newValue) {
    invalid.value = false;
    busy.value = false;
    oldPassword.value = "";
    newPassword.value = "";
  }
});
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
          @click="visible = false"></Button>
        <Button
          label="Confirm"
          size="small"
          type="submit"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
