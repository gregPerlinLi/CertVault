/* Wrapper */
export interface AbortOption {
  timeout?: number;
  signal?: AbortSignal;
}

export interface RestfulApiOption {
  method: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  baseUrl: string;
  pathNames?: Record<string, any>;
  searchParams?: Record<string, any>;
  payload?: any;
  abort?: AbortOption;
}

/* Parameters */
export interface BaseParams {
  abort?: AbortOption;
}

export interface PageParams<T extends string | undefined = undefined> {
  page?: number;
  limit?: number;
  keyword?: string;
  isAsc?: boolean;
  orderBy?: T;
}

/* Auxlilliary */
export interface PageVO<T> {
  total: number;
  list: T[] | null;
}

export interface ResultVO<T = null> {
  code: number;
  msg: string;
  timestamp: string;
  data: T | null;
}

/* Data */
export interface CaInfoDTO {
  uuid: string;
  algorithm: string;
  keySize: number;
  owner: string;
  parentCa: string | null;
  allowSubCa: boolean | null;
  comment: string;
  available: boolean;
  notBefore: string;
  notAfter: string;
  createdAt: string;
  modifiedAt: string;
}

export interface CertDetailDTO {
  subject: string;
  issuer: string;
  notBefore: string;
  notAfter: string;
  serialNumber: string;
  publicKey: {
    modulus?: string;
    publicExponent?: string;
    q?: {
      x: string | null;
      y: string | null;
      coordinateSystem: string | null;
    };
    w?: {
      affineX: string | null;
      affineY: string | null;
    };
    point?: {
      y: string;
      xodd: boolean;
    };
    encoded: string;
    format: string;
    algorithm: string;
    params: string | null;
  };
  extensions: any;
}

export interface CertInfoDTO {
  uuid: string;
  algorithm: string;
  keySize: number;
  caUuid: string;
  owner: string;
  comment: string;
  notBefore: string;
  notAfter: string;
  createAt: string;
  modifiedAt: string;
}

export interface LoginRecordDTO {
  uuid: string;
  username: string;
  ipAddress: string;
  region: string;
  province: string;
  city: string;
  browser: string;
  os: string;
  platform: string;
  loginTime: string;
  isOnline: boolean;
  isCurrentSession: boolean;
}

export interface OidcProviderDTO {
  provider: string;
  displayName: string;
  logo: string;
}

export interface UserProfileDTO {
  username: string;
  displayName: string;
  email: string;
  role: 1 | 2 | 3;
  isPasswordInitialized: boolean;
}

export interface ResponseCaDTO {
  uuid: string;
  algorithm: string;
  keySize: number;
  privkey: string | null;
  cert: string;
  notBefore: string;
  notAfter: string;
  comment: string;
}

export interface ResponseCertDTO {
  uuid: string;
  algorithm: string;
  keySize: number;
  privkey: string | null;
  cert: string;
  caUuid: string;
  notBefore: string;
  notAfter: string;
  comment: string;
}
