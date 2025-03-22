export const delay = async (ms: number): Promise<void> =>
  new Promise((res): void => {
    setTimeout(res, ms);
  });

export const createURLSearchParams = (
  params: Record<string, any>
): URLSearchParams => {
  const entries = Object.entries(params)
    .filter(([_, v]) => v !== undefined && v !== null)
    .map(([k, v]) => [k, String(v)]);
  return new URLSearchParams(Object.fromEntries(entries));
};
