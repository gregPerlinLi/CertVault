export interface ResultVO<T = null> {
  code: number;
  msg: string;
  timestamp: string;
  data: T | null;
}

export interface PaginationVO<T> {
  total: number;
  list: T[];
}

export interface UserProfileDTO {
  username: string;
  displayName: string;
  email: string;
  role: 1 | 2 | 3;
}

export interface CertInfoDTO {
  uuid: string;
  caUuid: string;
  owner: string;
  comment: string;
  notBefore: string;
  notAfter: string;
  createAt: string;
  modifiedAt: string;
}

export interface CertDetailDTO {
  subject: string;
  issuer: string;
  notBefore: string;
  notAfter: string;
  serialNumber: string;
  publicKey: {
    modulus: number;
    publicExponent: number;
    encoded: string;
    format: string;
    algorithm: string;
    params: string | null;
  };
  extensions: any;
}

export interface CAInfoDTO {
  uuid: string;
  owner: string;
  comment: string;
  available: boolean;
  notBefore: string;
  notAfter: string;
  createdAt: string;
  modifiedAt: string;
}
