import type { RestfulApiOption, ResultVO } from "@api/types";
import { useUserStore } from "@/stores/user";
import router from "@/router";

/* Utilities */
const createURLFromTemplate = (baseUrl: string, params: Record<string, any>) =>
  Object.entries(params)
    .filter(([_, v]) => typeof v === "string")
    .reduce(
      (prev, [k, v]) => prev.replace(`{${k}}`, encodeURIComponent(v)),
      baseUrl
    );

const createURLSearchParams = (params: Record<string, any>) =>
  new URLSearchParams(
    Object.entries(params)
      .filter(([_, v]) => v !== undefined && v !== null)
      .map(([k, v]) => [k, String(v)])
  );

// Wrapper
export const callRestfulApi = async <U = null>(
  opts: RestfulApiOption
): Promise<U> => {
  const pathname = createURLFromTemplate(opts.baseUrl, opts.pathNames ?? {});
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
      setTimeout(() => {
        user.clear();
        router.push("/");
      });
    }

    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};
