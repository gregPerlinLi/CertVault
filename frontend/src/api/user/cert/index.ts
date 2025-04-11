import type { AbortOption } from "@/api";
import type { CertDetailDTO } from "@/api/types";
import { callRestfulApi } from "@/api";

export const analyzeCert = (cert: string, abort?: AbortOption) =>
  callRestfulApi<CertDetailDTO>({
    method: "POST",
    baseUrl: "/api/v1/user/cert/analyze",
    payload: { cert },
    abort
  });
