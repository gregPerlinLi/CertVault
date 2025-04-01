<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { useNotify } from "@/utils/composable";

// Models
const visible = defineModel<boolean>("visible");

// Properties
const props = defineProps<{ value: string }>();

// Stores
const { syncToRemote } = useUserStore();

// Services
const { toast, info, error } = useNotify();

// Reactive
const invalid = ref(false);
const busy = ref(false);

const newEmail = ref(props.value);

// Actions
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

  // Try update
  busy.value = true;
  info("Info", "Updating");

  const err = await syncToRemote({ email: newEmail.value }, toast);
  if (err === null) {
    visible.value = false;
  }
  busy.value = false;
};
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
