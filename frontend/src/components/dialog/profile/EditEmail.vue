<script setup lang="ts">
import { getProfile, updateProfile } from "@/api/user/user";
import { useUserStore } from "@/stores/user";
import { useNotify } from "@/utils/composable";

/* Models */
const visible = defineModel<boolean>("visible");

// Properties
const props = defineProps<{ value: string }>();

/* Services */
const { toast, info, success, error } = useNotify();

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
    error("Validation Error", "New email is required");
    return;
  }
  if (newEmail.value === props.value) {
    invalid.value = true;
    error("Validation Error", "No changes found");
    return;
  }

  // Try to update
  busy.value = true;
  const msg = info("Info", "Updating");

  try {
    await updateProfile({ email: newEmail.value });
    const profile = await getProfile({ timeout: -1 });
    user.update(profile);
    success("Success", "Successfully updated profile");
    visible.value = false;
  } catch (err: unknown) {
    error("Fail to Update Profile", (err as Error).message);
  }

  toast.remove(msg);
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
          :disabled="busy"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
