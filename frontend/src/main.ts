import { createApp } from "vue";

// Vue plugins
import pluginRouter from "@/router";
import pluginPrimeVue from "primevue/config";
import pluginConfirmationService from "primevue/confirmationservice";
import pluginToastService from "primevue/toastservice";

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
  .use(pluginPrimeVue, {
    ripple: true,
    theme: {
      preset: Aura,
      options: {
        cssLayer: { name: "primevue", order: "theme, base, primevue" }
      }
    }
  })
  .use(pluginConfirmationService)
  .use(pluginToastService)
  .directive("tooltip", directiveTooltip)
  .mount("#app");
