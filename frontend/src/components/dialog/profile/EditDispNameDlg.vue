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

const newDisplayName = ref(props.value);

/* Actions */
const submit = async () => {
  // Clear error flag
  invalid.value = false;

  // Validate
  if (newDisplayName.value.length === 0) {
    invalid.value = true;
    warn("New display name is required");
    return;
  }
  if (newDisplayName.value === props.value) {
    invalid.value = true;
    warn("No changes found");
    return;
  }

  // Try to update
  busy.value = true;
  const msg = info("Updating");

  try {
    await updateProfile({ displayName: newDisplayName.value });
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
watch(visible, (newValue) => {
  if (newValue) {
    newDisplayName.value = props.value;
  } else {
    invalid.value = false;
    busy.value = false;
  }
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    header="Edit Display Name"
    :closable="false"
    modal>
    <form @submit.prevent="submit">
      <InputText
        v-model.trim="newDisplayName"
        class="mb-4 w-full"
        type="text"
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
