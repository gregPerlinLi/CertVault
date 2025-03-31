import type { ToastServiceMethods } from "primevue";
import type { ValiError } from "valibot";
import * as v from "valibot";

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

export const validateForm = (form: HTMLFormElement, schema: any) => {
  const obj: Record<string, string> = {};
  new FormData(form).forEach((v, k) => (obj[k] = v.toString().trim()));

  try {
    return { success: true, data: v.parse(schema, obj), error: undefined };
  } catch (err: unknown) {
    return { success: false, data: undefined, error: err as ValiError<any> };
  }
};
