<script setup lang="ts">
import { requestCaCert } from "@/api/admin/ca";
import { validateRequried } from "@/utils/validate";
import { useToast } from "primevue";
import YAML from "yaml";

// Models
const visible = defineModel<boolean>("visible");

// Reactive
const toast = useToast();

const busy = ref(false);
const errIdx = ref<number | null>(null);

const allowSubCa = ref(false);

// Actions
const onSubmit = async (ev: Event) => {
  // Parse form data
  const formData = new FormData(ev.target as HTMLFormElement);

  // Clear error index
  errIdx.value = null;

  // Validate data
  const country = validateRequried(
    formData,
    "country",
    toast,
    "Country is required"
  );
  if (country === null) {
    errIdx.value = 0;
    return;
  }

  const province = validateRequried(
    formData,
    "province",
    toast,
    "Province is required"
  );
  if (province === null) {
    errIdx.value = 1;
    return;
  }

  const city = validateRequried(formData, "city", toast, "City is required");
  if (city === null) {
    errIdx.value = 2;
    return;
  }

  const organization = validateRequried(
    formData,
    "organization",
    toast,
    "Organization is required"
  );
  if (organization === null) {
    errIdx.value = 3;
    return;
  }

  const organizationalUnit = validateRequried(
    formData,
    "organizational-unit",
    toast,
    "Organizational unit is required"
  );
  if (organizationalUnit === null) {
    errIdx.value = 4;
    return;
  }

  const commonName = validateRequried(
    formData,
    "common-name",
    toast,
    "Common name is required"
  );
  if (commonName === null) {
    errIdx.value = 5;
    return;
  }

  const expiry = parseInt(formData.get("expiry")!.toString().trim());

  const subjectAltNames = (() => {
    const raw = formData.get("subject-alt-names")?.toString().trim();
    if (raw === undefined || raw.length === 0) {
      return undefined;
    }

    try {
      const data = YAML.parse(raw);
      if (typeof data !== "object") {
        throw Error();
      }

      return Object.entries(data).map(([type, value]) => {
        if (typeof value !== "string") {
          throw Error();
        }
        return { type, value };
      });
    } catch {
      toast.add({
        severity: "error",
        summary: "Validation Error",
        detail: "Invalid subject alternative names format",
        life: 5000
      });
      return null;
    }
  })();
  if (subjectAltNames === null) {
    errIdx.value = 6;
    return;
  }

  const comment = formData.get("comment")?.toString().trim() ?? "";

  // Try request
  busy.value = true;
  toast.add({
    severity: "info",
    summary: "Info",
    detail: "Requesting",
    life: 3000
  });

  try {
    await requestCaCert({
      allowSubCa: allowSubCa.value,
      country,
      province,
      city,
      organization,
      organizationalUnit,
      commonName,
      expiry,
      comment,
      subjectAltNames
    });

    toast.add({
      severity: "success",
      summary: "Success",
      detail: "Successfully request new CA Certificate",
      life: 3000
    });
    visible.value = false;
  } catch (err: unknown) {
    toast.add({
      severity: "error",
      summary: "Fail to Request New CA Certificate",
      detail: (err as Error).message,
      life: 5000
    });
  } finally {
    busy.value = false;
  }
};
</script>

<template>
  <Dialog
    v-model:visible="visible"
    header="Request New CA Certificate"
    :closable="false"
    modal>
    <form @submit.prevent="onSubmit">
      <div class="flex gap-8">
        <section class="w-1/3">
          <label for="country" required>Country</label>
          <InputText
            id="country"
            name="country"
            placeholder="e.g. CN"
            size="small"
            :invalid="errIdx === 0"
            required />
        </section>
        <section class="w-1/3">
          <label for="province" required>Province</label>
          <InputText
            id="province"
            name="province"
            placeholder="e.g. Guangdong"
            size="small"
            :invalid="errIdx === 1"
            required />
        </section>
        <section class="w-1/3">
          <label for="city" required>City</label>
          <InputText
            id="city"
            name="city"
            placeholder="e.g. Guangzhou"
            size="small"
            :invalid="errIdx === 2"
            required />
        </section>
      </div>
      <div class="flex gap-8">
        <section class="w-1/2">
          <label for="organization" required>Organization</label>
          <InputText
            id="organization"
            name="organization"
            size="small"
            :invalid="errIdx === 3"
            required />
        </section>
        <section class="w-1/2">
          <label for="organizational-unit" required>Organizational Unit</label>
          <InputText
            id="organizational-unit"
            name="organizational-unit"
            size="small"
            :invalid="errIdx === 4"
            required />
        </section>
      </div>
      <div class="flex gap-8">
        <section class="w-1/3">
          <label for="common-name" required>Common Name</label>
          <InputText
            id="common-name"
            name="common-name"
            size="small"
            :invalid="errIdx === 5"
            required />
        </section>
        <section class="w-1/3">
          <label for="expiry" required>Expiry</label>
          <InputNumber
            id="expiry"
            name="expiry"
            size="small"
            suffix=" day(s)"
            :default-value="30"
            :max="356"
            :min="1"
            required />
        </section>
        <section class="w-1/3">
          <label for="allow-sub-ca">Allow Sub CA</label>
          <ToggleButton v-model="allowSubCa" id="allow-sub-ca" size="small" />
        </section>
      </div>
      <section>
        <label for="subject-alt-names">Subject Alternative Names</label>
        <Textarea
          id="subject-alt-names"
          name="subject-alt-names"
          placeholder="Key-value pair each line, in YAML format"
          size="small"
          :invalid="errIdx === 6"></Textarea>
      </section>
      <section>
        <label for="comment">Certificate Comment</label>
        <InputText id="comment" name="comment" size="small" />
      </section>
      <div class="flex justify-end gap-2 mt-4">
        <Button
          label="Cancel"
          severity="secondary"
          size="small"
          type="button"
          :disabled="busy"
          @click="visible = false"></Button>
        <Button
          label="Request"
          size="small"
          type="submit"
          :disabled="busy"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>

<style scoped>
@import "tailwindcss";

section {
  @apply flex flex-col gap-1 my-2;

  label {
    @apply font-bold;

    &[required]::after {
      @apply text-red-500;
      content: "*";
    }
  }
}
</style>
