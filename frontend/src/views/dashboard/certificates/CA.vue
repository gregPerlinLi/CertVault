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
        label: 'CA Certificates',
        icon: 'pi pi-building-columns'
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
  <CertMgr variant="ca" />
</template>
