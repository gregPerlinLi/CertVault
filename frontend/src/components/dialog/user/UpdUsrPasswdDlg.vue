<script setup lang="ts">
import type { UserProfileDTO } from "@/api/types";
import { updateUsrInfo } from "@/api/superadmin/user";

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
const props = defineProps<{ user: UserProfileDTO | null }>();

/* Services */
const { success, info, error, remove } = useNotify();

/* Reactive */
const invalid = ref(false);
const busy = ref(false);
const newPassword = ref("");

/* Actions */
const submit = async () => {
  // Check properties
  if (props.user === null) {
    return;
  }

  // Clear error flag
  invalid.value = false;

  // Validate
  if (newPassword.value.length === 0) {
    invalid.value = true;
    error("New password is required", "Validation Error");
    return;
  }

  // Try to update
  busy.value = true;
  const msg = info("Updating");

  try {
    await updateUsrInfo({
      username: props.user.username,
      newPassword: newPassword.value
    });
    success("Successfully updated");
    visible.value = false;
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Update");
  }

  remove(msg);
  busy.value = false;
};

/* Watches */
watch(visible, () => {
  if (!visible.value) {
    invalid.value = false;
    busy.value = false;
    newPassword.value = "";
  }
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    :closable="false"
    :header="`Update Password for ${user?.displayName}`"
    modal>
    <form @submit.prevent="submit">
      <Password
        v-model="newPassword"
        class="mb-4 w-full"
        input-class="w-full"
        placeholder="New Password"
        :disabled="busy"
        :invalid="invalid"
        toggle-mask />
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
