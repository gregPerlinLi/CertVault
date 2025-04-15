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

export interface AbortOption {
  timeout?: number;
  signal?: AbortSignal;
}
export interface RestfulApiOption {
  method: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  baseUrl: string;
  pathNames?: Record<string, string>;
  searchParams?: Record<string, any>;
  payload?: any;
  abort?: AbortOption;
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
    signal: AbortSignal.any(
      [
        typeof opts.abort?.timeout === "number"
          ? opts.abort.timeout < 0
            ? null
            : AbortSignal.timeout(opts.abort.timeout)
          : AbortSignal.timeout(10000),
        opts.abort?.signal ?? null
      ].filter((s) => s !== null)
    )
  } satisfies RequestInit;

  const resp = await fetch(uri, req);
  if (!resp.ok) {
    throw Error(`HTTP: ${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<U> = await resp.json();
  if (json.code < 200 || json.code >= 300) {
    const user = useUserStore();
    if (json.code === 401 && user.isSignedIn.value) {
      user.clear();
      setTimeout(() => router.push("/"));
    }

    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};
