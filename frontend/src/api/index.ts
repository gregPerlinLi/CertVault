import type { ResultVO } from "@/api/types";

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
  pathNames?: string[];
  searchParams?: Record<string, any>;
  payload?: any;
  jsonParser?: typeof JSON.parse;
}
export const callRestfulApi = async <U = null>(
  opts: RestfulApiOption
): Promise<U> => {
  const pathname = [opts.baseUrl, ...(opts.pathNames ?? [])].join("/");
  const params = createURLSearchParams(opts.searchParams ?? {});
  const uri = params.size > 0 ? `${pathname}?${params}` : pathname;

  const req = {
    method: opts.method,
    headers:
      opts.method !== "GET" && opts.method !== "DELETE"
        ? { "Content-Type": "application/json" }
        : undefined,
    body: opts.payload !== undefined ? JSON.stringify(opts.payload) : undefined
  } satisfies RequestInit;

  const resp = await fetch(uri, req);
  if (!resp.ok) {
    throw Error(`HTTP: ${resp.statusText} (${resp.status})`);
  }

  const json: ResultVO<U> =
    opts.jsonParser === undefined
      ? await resp.json()
      : opts.jsonParser(await resp.text());
  if (json.code !== 200) {
    throw Error(`${json.msg} (${json.code})`);
  }

  return json.data!;
};
