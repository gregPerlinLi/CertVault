import type { ToastServiceMethods } from "primevue";

export const validateRequried = (
  data: FormData,
  name: string,
  toast?: ToastServiceMethods,
  toastDetails?: string
) => {
  const value = data.get(name)?.toString().trim();
  if (value === undefined || value.length === 0) {
    toast?.add({
      severity: "error",
      summary: "Validation Error",
      detail: toastDetails ?? `${name} is required`,
      life: 5000
    });
    return null;
  }

  return value;
};
