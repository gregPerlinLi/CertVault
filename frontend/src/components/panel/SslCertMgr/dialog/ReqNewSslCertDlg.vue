<script setup lang="ts">
import type { CaInfoDTO } from "@/api/types";
import type { VirtualScrollerLazyEvent } from "primevue";
import { getAllBindedCaInfo } from "@/api/user/cert/ca";
import { requestSslCert } from "@/api/user/cert/ssl";
import { useNotify } from "@/utils/composable";

// Models
const visible = defineModel<boolean>("visible");

// Emits
const emits = defineEmits<{ success: [] }>();

// Services
const { info, success, error } = useNotify();

// Reactives
const busy = ref(false);
const errField = ref<string>();

const loadingCa = ref(false);
const caList = ref<CaInfoDTO[]>([]);
const selectedCaUuid = ref<string>();

// Actions
const onLazyLoadCa = async (ev: VirtualScrollerLazyEvent) => {
  try {
    loadingCa.value = true;

    const page = await getAllBindedCaInfo(Math.trunc(ev.first / 30) + 1, 30);
    if (page.list !== null) {
      const tmp = caList.value.slice();
      for (let i = ev.first; i < ev.first + page.list.length; i++) {
        tmp[i] = page.list[i - ev.first];
      }
      caList.value = tmp;
    }
  } catch (err: unknown) {
    if (visible) {
      error("Fail to Get Binded CA List", (err as Error).message);
    }
  } finally {
    loadingCa.value = false;
  }
};
const onSubmit = async (ev: Event) => {
  errField.value = undefined;
  const formData = new FormData(ev.target as HTMLFormElement);

  const caUuid = selectedCaUuid.value ?? null;
  if (caUuid === null) {
    errField.value = "ca-uuid";
    error("Validation Error", "CA is required");
    return;
  }

  const country = formData.get("country")!.toString().trim();
  if (country.length === 0) {
    errField.value = "country";
    error("Validation Error", "Country is required");
    return;
  }

  const province = formData.get("province")!.toString().trim();
  if (province.length === 0) {
    errField.value = "province";
    error("Validation Error", "Province is required");
    return;
  }

  const city = formData.get("city")!.toString().trim();
  if (city.length === 0) {
    errField.value = "city";
    error("Validation Error", "City is required");
    return;
  }

  const organization = formData.get("organization")!.toString().trim();
  if (organization.length === 0) {
    errField.value = "organization";
    error("Validation Error", "Organization is required");
    return;
  }

  const organizationalUnit = formData
    .get("organizational-unit")!
    .toString()
    .trim();
  if (organizationalUnit.length === 0) {
    errField.value = "organizational-unit";
    error("Validation Error", "Organizational unit is required");
    return;
  }

  const commonName = formData.get("common-name")!.toString().trim();
  if (commonName.length === 0) {
    errField.value = "common-name";
    error("Validation Error", "Common name is required");
    return;
  }

  const expiry = parseInt(formData.get("expiry")!.toString().trim());

  const subjectAltNames = (() => {
    const raw = formData.get("subject-alt-names")!.toString().trim();
    if (raw.length === 0) {
      return undefined;
    }

    const lines = raw
      .split("\n")
      .map((str) => str.trim())
      .filter((str) => str.length > 0)
      .map((str) => str.split("=", 2).map((str) => str.trim()));

    const SAN_TYPE = [
      "DNS_NAME",
      "IP_ADDRESS",
      "URI",
      "EMAIL",
      "DIRECTORY_NAME",
      "EDI_PARTY_NAME"
    ];
    for (const pair of lines) {
      if (!SAN_TYPE.includes(pair[0])) {
        errField.value = "subject-alt-names";
        error("Validation Error", "Invalid SAN type");
        return null;
      }
      if (pair.length !== 2) {
        errField.value = "subject-alt-names";
        error("Validation Error", "Invalid SAN format");
        return null;
      }
    }

    return lines.map(([type, value]) => ({ type, value }));
  })();
  if (subjectAltNames === null) {
    return;
  }

  const comment = formData.get("comment")?.toString().trim() ?? "";

  // Try request
  try {
    busy.value = true;
    info("Info", "Requesting");

    await requestSslCert({
      caUuid,
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

    success("Success", "Successfully requested");
    emits("success");
    visible.value = false;
  } catch (err: unknown) {
    error("Fail to Request", (err as Error).message);
  } finally {
    busy.value = false;
  }
};
</script>

<template>
  <Dialog
    v-model:visible="visible"
    header="Request New SSL Certificate"
    :closable="false"
    modal>
    <form @submit.prevent="onSubmit">
      <section>
        <label required>CA for Issuance</label>
        <Select
          v-model="selectedCaUuid"
          option-label="uuid"
          option-value="uuid"
          placeholder="Select a CA"
          size="small"
          :invalid="errField === 'ca-uuid'"
          :options="caList"
          :virtual-scroller-options="{
            delay: 250,
            itemSize: 30,
            lazy: true,
            loading: loadingCa,
            onLazyLoad: onLazyLoadCa,
            showLoader: true
          }">
          <template #option="slotProps">
            <p
              v-tooltip.left="{
                value: slotProps.option.uuid,
                class: 'text-sm -translate-x-3'
              }"
              class="w-full">
              {{ slotProps.option.comment }}
            </p>
          </template>
        </Select>
      </section>
      <div class="flex gap-8">
        <section class="w-1/3">
          <label for="country" required>Country</label>
          <InputText
            id="country"
            name="country"
            placeholder="e.g. CN"
            size="small"
            :invalid="errField === 'country'"
            required />
        </section>
        <section class="w-1/3">
          <label for="province" required>Province</label>
          <InputText
            id="province"
            name="province"
            placeholder="e.g. Guangdong"
            size="small"
            :invalid="errField === 'province'"
            required />
        </section>
        <section class="w-1/3">
          <label for="city" required>City</label>
          <InputText
            id="city"
            name="city"
            placeholder="e.g. Guangzhou"
            size="small"
            :invalid="errField === 'city'"
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
            :invalid="errField === 'organization'"
            required />
        </section>
        <section class="w-1/2">
          <label for="organizational-unit" required>Organizational Unit</label>
          <InputText
            id="organizational-unit"
            name="organizational-unit"
            size="small"
            :invalid="errField === 'organizational-unit'"
            required />
        </section>
      </div>
      <div class="flex gap-8">
        <section class="w-2/3">
          <label for="common-name" required>Common Name</label>
          <InputText
            id="common-name"
            name="common-name"
            size="small"
            :invalid="errField === 'common-name'"
            required />
        </section>
        <section class="w-1/3">
          <label for="expiry" required>Expiry</label>
          <InputNumber
            input-id="expiry"
            name="expiry"
            size="small"
            suffix=" day(s)"
            :default-value="30"
            :max="365"
            :min="1"
            required
            show-buttons />
        </section>
      </div>
      <section>
        <label for="subject-alt-names">Subject Alternative Names</label>
        <Textarea
          class="min-h-24"
          id="subject-alt-names"
          name="subject-alt-names"
          :placeholder="'Each line is a key-value pair seperated by a \'=\'\ne.g. \'IP_ADDRESS=1.1.1.1\''"
          :invalid="errField === 'subject-alt-names'"
          size="small"></Textarea>
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
}
</style>
