import type { ToastMessageOptions } from "primevue/toast";
import { useToast } from "primevue/usetoast";

export const useNotify = () => {
  const toast = useToast();

  const success = (
    detail: string,
    summary: string = "Success",
    life: number = 2500
  ) => {
    const msg = {
      severity: "success",
      summary,
      detail,
      life
    } satisfies ToastMessageOptions;
    toast.add(msg);
    return msg;
  };
  const info = (
    detail: string,
    summary: string = "Info",
    life: number = -1
  ) => {
    const msg = {
      severity: "info",
      summary,
      detail,
      life
    } satisfies ToastMessageOptions;
    toast.add(msg);
    return msg;
  };
  const warn = (
    detail: string,
    summary: string = "Warn",
    life: number = 4000
  ) => {
    const msg = {
      severity: "warn",
      summary,
      detail,
      life
    } satisfies ToastMessageOptions;
    toast.add(msg);
    return msg;
  };
  const error = (
    detail: string,
    summary: string = "Error",
    life: number = 5000
  ) => {
    const msg = {
      severity: "error",
      summary,
      detail,
      life
    } satisfies ToastMessageOptions;
    toast.add(msg);
    return msg;
  };
  const secondary = (
    detail: string,
    summary: string = "Secondary",
    life: number = 2000
  ) => {
    const msg = {
      severity: "secondary",
      summary,
      detail,
      life
    } satisfies ToastMessageOptions;
    toast.add(msg);
    return msg;
  };
  const contrast = (
    detail: string,
    summary: string = "Contrast",
    life: number = 3000
  ) => {
    const msg = {
      severity: "contrast",
      summary,
      detail,
      life
    } satisfies ToastMessageOptions;
    toast.add(msg);
    return msg;
  };
  const remove = (msg: ToastMessageOptions) => toast.remove(msg);
  const removeAll = () => toast.removeAllGroups();

  return { success, info, warn, error, secondary, contrast, remove, removeAll };
};
