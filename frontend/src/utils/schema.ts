import { z } from "zod";

export const reqNewCertSchema = z
  .object({
    country: z.string().regex(/^[A-Z]{2}$/),
    province: z.string().nonempty(),
    city: z.string().nonempty(),
    organization: z.string().nonempty(),
    "organizational-unit": z.string().nonempty(),
    "common-name": z.string().nonempty(),
    expiry: z.preprocess(
      (v) => parseInt(v as string),
      z.number().min(1).max(365)
    ),
    "subject-alt-names": z.preprocess(
      (v) =>
        (v as string)
          .split("\n")
          .map((str) => str.trim())
          .filter((str) => str.length > 0)
          .map((str) => str.split("=", 2).map((str) => str.trim()))
          .map(([type, value]) => ({ type, value })),
      z.array(
        z.object({
          type: z.enum([
            "DNS_NAME",
            "IP_ADDRESS",
            "URI",
            "EMAIL",
            "DIRECTORY_NAME",
            "EDI_PARTY_NAME"
          ]),
          value: z
            .string({ message: "Invalid SAN format" })
            .nonempty("SAN value is required")
        })
      )
    ),
    comment: z.string().nonempty()
  })
  .transform((obj) => ({
    country: obj.country,
    province: obj.province,
    city: obj.city,
    organization: obj.organization,
    organizationalUnit: obj["organizational-unit"],
    commonName: obj["common-name"],
    expiry: obj.expiry,
    subjectAltNames: obj["subject-alt-names"],
    comment: obj.comment
  }));
