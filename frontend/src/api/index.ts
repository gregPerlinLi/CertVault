import type { ResultVO } from "@/api/types";
import { useUserStore } from "@/stores/user";
import router from "@/router";

export const createURLSearchParams = (
  params: Record<string, any>
): URLSearchParams => {
  const entries = Object.entries(params)
    .filter(([_, v]) => v !== undefined && v !== null)
    .map(([k, v]) => [k, String(v)]);
  return new URLSearchParams(Object.fromEntries(entries));
};

export interface RestfulApiOption {
  method: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  baseUrl: string;
  pathNames?: Record<string, string>;
  searchParams?: Record<string, any>;
  payload?: any;
}
export const callRestfulApi = async <U = null>(
  opts: RestfulApiOption
): Promise<U> => {
  const pathname = Object.entries(opts.pathNames ?? {}).reduce(
    (prev, [k, v]) => prev.replace(`{${k}}`, encodeURIComponent(v)),
    opts.baseUrl
  );
  const params = createURLSearchParams(opts.searchParams ?? {});
  const uri = params.size > 0 ? `${pathname}?${params}` : pathname;

  const req = {
    method: opts.method,
    headers:
      opts.method !== "GET" && opts.method !== "DELETE"
        ? { "Content-Type": "application/json" }
        : undefined,
    body: opts.payload !== undefined ? JSON.stringify(opts.payload) : undefined,
    signal: AbortSignal.timeout(10000)
  } satisfies RequestInit;

  const resp = await fetch(uri, req);
  if (!resp.ok) {
    throw Error(`HTTP: ${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<U> = await resp.json();
  if (json.code < 200 || json.code >= 300) {
    const { signedIn } = useUserStore();
    if (json.code === 401 && signedIn.value) {
      signedIn.value = false;
      router.push("/");
    }

    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};
