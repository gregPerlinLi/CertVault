<template>
  <div class="flex flex-col">
    <AppHeader />
    <div class="flex">
      <AppSidebar />
      <main class="grow p-4">
        <RouterView />
      </main>
    </div>
  </div>
</template>
