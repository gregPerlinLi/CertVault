<template>
  <Toast
    :pt="{
      summary: { class: 'leading-none' },
      detail: { class: 'leading-none' }
    }" />
  <ConfirmDialog />
  <RouterView />
</template>
