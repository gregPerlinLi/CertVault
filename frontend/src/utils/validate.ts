import type { ValiError } from "valibot";
import * as v from "valibot";

export const validateForm = (form: HTMLFormElement, schema: any) => {
  const obj: Record<string, string> = {};
  new FormData(form).forEach((v, k) => (obj[k] = v.toString().trim()));

  try {
    return { success: true, data: v.parse(schema, obj), error: undefined };
  } catch (err: unknown) {
    return { success: false, data: undefined, error: err as ValiError<any> };
  }
};
