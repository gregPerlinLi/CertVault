<script setup lang="ts">
useTitle("Certificates - CertVault");
</script>

<template>
  <!-- Header -->
  <h1 class="font-bold mb-4 text-2xl">
    <i class="mr-2 pi pi-verified text-xl"></i>Certificates
  </h1>

  <!-- Main -->
  <Tabs value="0">
    <TabList class="text-sm">
      <Tab value="0" class="py-2">CA Certificates</Tab>
      <Tab value="1" class="py-2">SSL Certificates</Tab>
    </TabList>
    <TabPanels>
      <TabPanel value="0">
        <CaCertManager />
      </TabPanel>
      <TabPanel value="1">
        <SslCertMgr />
      </TabPanel>
    </TabPanels>
  </Tabs>
</template>
