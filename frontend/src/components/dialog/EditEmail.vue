<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { useToast } from "primevue/usetoast";

// Models
const visible = defineModel<boolean>("visible");

// Properties
const props = defineProps<{ value: string }>();

// Stores
const { syncToRemote } = useUserStore();

// Reactive
const toast = useToast();

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
    toast.add({
      severity: "error",
      summary: "Validation Error",
      detail: "New email is required",
      life: 5000
    });
    return;
  }
  if (newEmail.value === props.value) {
    invalid.value = true;
    toast.add({
      severity: "error",
      summary: "Validation Error",
      detail: "No changes found",
      life: 5000
    });
    return;
  }

  // Try update
  busy.value = true;
  toast.add({
    severity: "info",
    summary: "Info",
    detail: "Updating",
    life: 3000
  });

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
