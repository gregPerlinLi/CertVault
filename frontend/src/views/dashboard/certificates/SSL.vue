<template>
  <!-- Header -->
  <Breadcrumb
    class="mb-4"
    :home="{ icon: 'pi pi-home' }"
    :model="[
      {
        label: 'Certificates',
        icon: 'pi pi-verified'
      },
      {
        label: 'SSL Certificates',
        icon: 'pi pi-receipt'
      }
    ]">
    <template #item="{ item }">
      <div class="flex gap-2 items-center">
        <span :class="item.icon"></span>
        <span>{{ item.label }}</span>
      </div>
    </template>
  </Breadcrumb>

  <!-- Main -->
  <CertMgr variant="ssl" />
</template>
