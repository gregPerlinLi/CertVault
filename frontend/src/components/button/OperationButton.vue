<script setup lang="ts">
import type { ButtonProps } from "primevue/button";

// Properties
defineProps<{
  icon: string;
  label: string;
  severity?: ButtonProps["severity"];
}>();
</script>

<template>
  <Button
    v-tooltip.top="{ value: label, class: 'text-sm' }"
    class="h-6 w-6"
    size="small"
    variant="text"
    :aria-label="label"
    :icon="`pi ${icon}`"
    :severity="severity"
    rounded></Button>
</template>
