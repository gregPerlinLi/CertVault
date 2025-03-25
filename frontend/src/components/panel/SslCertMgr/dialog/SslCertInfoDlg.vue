<script setup lang="ts">
import type { CertDetailDTO, CertInfoDTO } from "@/api/types";
import { analyzeCert } from "@/api/user/cert";
import { getSslCert } from "@/api/user/cert/ssl";
import { useNotify } from "@/utils/composable";
import { nanoid } from "nanoid";

// Models
const visible = defineModel<boolean>("visible");

// Properties
const props = defineProps<{ data?: CertInfoDTO }>();

// Services
const { error } = useNotify();

// Reactive
const details = ref<CertDetailDTO>();
const canRetry = ref(false);

// Non-reactive
let nonce = nanoid();

// Actions
const fetchDetails = async () => {
  if (props.data === undefined) {
    return;
  }

  nonce = nanoid();
  const tag = nonce;

  details.value = undefined;
  canRetry.value = false;

  try {
    const cert = await getSslCert(props.data!.uuid);
    if (tag !== nonce) {
      return;
    }

    const data = await analyzeCert(cert);
    if (tag !== nonce) {
      return;
    }

    details.value = data;
  } catch (err: unknown) {
    if (tag === nonce && visible) {
      canRetry.value = true;
      error("Fail to Get Detailed Info", (err as Error).message);
    }
  }
};

// Watch
watch(visible, async (v) => {
  if (v) {
    await fetchDetails();
  }
});
</script>

<template>
  <Dialog v-model:visible="visible" header="SSL Certificate Information" modal>
    <Tabs value="0">
      <TabList class="text-sm">
        <Tab value="0" class="py-2">Basic Info</Tab>
        <Tab value="1" class="py-2">Detailed Info</Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <Accordion class="w-120">
            <AccordionPanel value="comment">
              <AccordionHeader>Comment</AccordionHeader>
              <AccordionContent>
                <pre>{{ data?.comment }}</pre>
              </AccordionContent>
            </AccordionPanel>
            <AccordionPanel value="owner">
              <AccordionHeader>Owner</AccordionHeader>
              <AccordionContent>
                <pre>{{ data?.owner }}</pre>
              </AccordionContent>
            </AccordionPanel>
            <AccordionPanel value="ca-uuid">
              <AccordionHeader>CA UUID</AccordionHeader>
              <AccordionContent>
                <pre>{{ data?.caUuid }}</pre>
              </AccordionContent>
            </AccordionPanel>
            <AccordionPanel value="uuid">
              <AccordionHeader>UUID</AccordionHeader>
              <AccordionContent>
                <pre>{{ data?.uuid }}</pre>
              </AccordionContent>
            </AccordionPanel>
            <AccordionPanel value="not-before">
              <AccordionHeader>Not Before</AccordionHeader>
              <AccordionContent>
                <pre>{{ data?.notBefore }}</pre>
              </AccordionContent>
            </AccordionPanel>
            <AccordionPanel value="not-after">
              <AccordionHeader>Not After</AccordionHeader>
              <AccordionContent>
                <pre>{{ data?.notAfter }}</pre>
              </AccordionContent>
            </AccordionPanel>
          </Accordion>
        </TabPanel>
        <TabPanel value="1" class="w-120">
          <template v-if="details === undefined">
            <ProgressSpinner
              :pt="{
                root: { class: 'flex justify-center my-4' }
              }" />
            <Button
              v-if="canRetry"
              class="w-full"
              icon="pi pi-refresh"
              label="Retry"
              severity="info"
              size="small"
              @click="fetchDetails"></Button>
          </template>
          <template v-else>
            <Accordion>
              <AccordionPanel value="subject">
                <AccordionHeader>Subject</AccordionHeader>
                <AccordionContent>
                  <pre>{{ details?.subject }}</pre>
                </AccordionContent>
              </AccordionPanel>
              <AccordionPanel value="issuer">
                <AccordionHeader>Issuer</AccordionHeader>
                <AccordionContent>
                  <pre>{{ details?.issuer }}</pre>
                </AccordionContent>
              </AccordionPanel>
              <AccordionPanel value="serial-number">
                <AccordionHeader>Serial Number</AccordionHeader>
                <AccordionContent>
                  <pre>{{ details?.serialNumber }}</pre>
                </AccordionContent>
              </AccordionPanel>
              <AccordionPanel value="modulus">
                <AccordionHeader>Modulus</AccordionHeader>
                <AccordionContent>
                  <pre>{{ details?.publicKey.modulus }}</pre>
                </AccordionContent>
              </AccordionPanel>
              <AccordionPanel value="public-exponent">
                <AccordionHeader>Public Exponent</AccordionHeader>
                <AccordionContent>
                  <pre>{{ details?.publicKey.publicExponent }}</pre>
                </AccordionContent>
              </AccordionPanel>
              <AccordionPanel value="encoded">
                <AccordionHeader>Encoded</AccordionHeader>
                <AccordionContent>
                  <pre>{{ details?.publicKey.encoded }}</pre>
                </AccordionContent>
              </AccordionPanel>
              <AccordionPanel value="format">
                <AccordionHeader>Format</AccordionHeader>
                <AccordionContent>
                  <pre>{{ details?.publicKey.format }}</pre>
                </AccordionContent>
              </AccordionPanel>
              <AccordionPanel value="algorithm">
                <AccordionHeader>Algorithm</AccordionHeader>
                <AccordionContent>
                  <pre>{{ details?.publicKey.algorithm }}</pre>
                </AccordionContent>
              </AccordionPanel>
              <AccordionPanel value="parameters">
                <AccordionHeader>Parameters</AccordionHeader>
                <AccordionContent>
                  <pre>{{ details?.publicKey.params ?? "null" }}</pre>
                </AccordionContent>
              </AccordionPanel>
              <AccordionPanel value="extensions">
                <AccordionHeader>Extensions</AccordionHeader>
                <AccordionContent>
                  <pre>{{
                    JSON.stringify(details?.extensions, undefined, 2)
                  }}</pre>
                </AccordionContent>
              </AccordionPanel>
            </Accordion>
          </template>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </Dialog>
</template>

<style scoped>
@import "tailwindcss";

pre {
  @apply break-all p-4 whitespace-break-spaces;
}

.p-accordionheader {
  @apply text-sm p-2;
}
</style>
