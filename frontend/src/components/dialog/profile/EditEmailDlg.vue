<script setup lang="ts">
import { getProfile, updateProfile } from "@api/user/user";

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
const props = defineProps<{ value: string }>();

/* Services */
const { success, info, warn, error, remove } = useNotify();

/* Stores */
const user = useUserStore();

/* Reactive */
const invalid = ref(false);
const busy = ref(false);

const newEmail = ref(props.value);

/* Actions */
const submit = async () => {
  // Clear error flag
  invalid.value = false;

  // Validate
  if (newEmail.value.length === 0) {
    invalid.value = true;
    warn("New email is required");
    return;
  }
  if (newEmail.value === props.value) {
    invalid.value = true;
    warn("No changes found");
    return;
  }

  // Try to update
  busy.value = true;
  const msg = info("Updating");

  try {
    await updateProfile({ email: newEmail.value });
    const profile = await getProfile({ abort: { timeout: -1 } });
    user.update(profile);
    visible.value = false;
    success("Successfully updated profile");
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Update Profile");
  }

  remove(msg);
  busy.value = false;
};

/* Watches */
watch(visible, () => {
  if (!visible.value) {
    invalid.value = false;
    busy.value = false;
    newEmail.value = props.value;
  }
});
</script>

<template>
  <Dialog v-model:visible="visible" header="Edit Email" :closable="false" modal>
    <form @submit.prevent="submit">
      <InputText
        v-model.trim="newEmail"
        class="mb-4 w-full"
        type="email"
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
          label="Save"
          size="small"
          type="submit"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
