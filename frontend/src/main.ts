import { createApp } from "vue";

// Vue plugins
import pluginRouter from "@/router";
import pluginNotifications from "@kyvg/vue3-notification";
import pluginPrimeVue from "primevue/config";

// Vue directive
import directiveTooltip from "primevue/tooltip";

// Resources
import Aura from "@primeuix/themes/aura";

// Global stylesheets
import "@/main.css";
import "primeicons/primeicons.css";

// Root component
import App from "@/App.vue";

// Create application
createApp(App)
  .use(pluginRouter)
  .use(pluginNotifications)
  .use(pluginPrimeVue, { ripple: true, theme: { preset: Aura } })
  .directive("tooltip", directiveTooltip)
  .mount("#app");
