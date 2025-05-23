import * as v from "valibot";

export const useFormValidator = (
  schema: v.BaseSchema<any, any, v.BaseIssue<unknown>>
) => {
  const errField = ref<string | null>(null);

  const isInvalid = (field: string) => errField.value === field;
  const setInvalid = (s: string) => (errField.value = s);
  const clearInvalid = (field: string) => {
    if (errField.value === field) {
      errField.value = null;
    }
  };
  const validate = (el: HTMLFormElement) => {
    errField.value = null;

    const data = new FormData(el);
    const obj = Array.from(data.entries()).reduce(
      (prev, [k, v]) => {
        prev[k] = v.toString();
        return prev;
      },
      {} as Record<string, string>
    );

    const { success, output, issues } = v.safeParse(schema, obj);
    if (!success) {
      errField.value = issues[0].path![0].key as string;
    }

    return { success, output, issues: issues ?? null };
  };

  return { isInvalid, setInvalid, clearInvalid, validate };
};
