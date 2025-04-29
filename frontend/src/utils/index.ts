import type { LoginRecordDTO } from "@/api/types";

export const b64ToU8Arr = (b64: string) => {
  const bin = window.atob(b64);
  const ret = new Uint8Array(bin.length);
  for (let i = 0; i < bin.length; i++) {
    ret[i] = bin.charCodeAt(i);
  }
  return ret;
};

export const saveFile = (filename: string, data: Blob) => {
  const url = URL.createObjectURL(data);
  const el = document.createElement("a");
  el.download = filename;
  el.href = url;
  el.click();
  URL.revokeObjectURL(url);
};

export const parseIpRegion = (dto: LoginRecordDTO) => {
  const arr = [dto.region, dto.province, dto.city]
    .map((s) => s.trim())
    .filter((s) => s.length > 0 && s !== "Unknown");
  return arr.length === 0 ? "Unknown" : arr.join(", ");
};
