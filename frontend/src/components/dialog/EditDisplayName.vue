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

const newDisplayName = ref(props.value);

// Actions
const submit = async () => {
  // Clear error flag
  invalid.value = false;

  // Validate
  if (newDisplayName.value.length === 0) {
    invalid.value = true;
    toast.add({
      severity: "error",
      summary: "Validation Error",
      detail: "New display name is required",
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

  const err = await syncToRemote({ displayName: newDisplayName.value }, toast);
  if (err === null) {
    visible.value = false;
  } else {
    busy.value = false;
  }
};
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
          :disabled="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
