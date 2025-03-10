export const delay = async (ms: number): Promise<void> =>
  new Promise((res): void => {
    setTimeout(res, ms);
  });
