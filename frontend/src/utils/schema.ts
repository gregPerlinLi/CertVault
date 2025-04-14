import * as v from "valibot";

export const signInSchema = v.object({
  username: v.pipe(v.string(), v.trim(), v.nonEmpty()),
  password: v.pipe(v.string(), v.trim(), v.nonEmpty())
});

export const reqNewCertSchema = v.pipe(
  v.object({
    country: v.pipe(v.string(), v.trim(), v.regex(/^[A-Z]{2}$/)),
    province: v.pipe(v.string(), v.trim(), v.nonEmpty()),
    city: v.pipe(v.string(), v.trim(), v.nonEmpty()),
    organization: v.pipe(v.string(), v.trim(), v.nonEmpty()),
    "organizational-unit": v.pipe(v.string(), v.trim(), v.nonEmpty()),
    "common-name": v.pipe(v.string(), v.trim(), v.nonEmpty()),
    expiry: v.pipe(
      v.string(),
      v.trim(),
      v.transform(parseInt),
      v.number(),
      v.minValue(1),
      v.maxValue(365)
    ),
    "subject-alt-names": v.pipe(
      v.string(),
      v.transform((v) =>
        (v as string)
          .split("\n")
          .map((str) => str.trim())
          .filter((str) => str.length > 0)
          .map((str) => str.split("=", 2).map((str) => str.trim()))
          .map(([type, value]) => ({ type, value }))
      ),
      v.array(
        v.object({
          type: v.pipe(
            v.string(),
            v.picklist([
              "DNS_NAME",
              "IP_ADDRESS",
              "URI",
              "EMAIL",
              "DIRECTORY_NAME",
              "EDI_PARTY_NAME"
            ])
          ),
          value: v.pipe(v.string("Invalid SAN format"), v.trim())
        })
      )
    ),
    comment: v.pipe(v.string(), v.trim(), v.nonEmpty())
  }),
  v.transform((obj) => ({
    country: obj.country,
    province: obj.province,
    city: obj.city,
    organization: obj.organization,
    organizationalUnit: obj["organizational-unit"],
    commonName: obj["common-name"],
    expiry: obj.expiry,
    subjectAltNames: obj["subject-alt-names"],
    comment: obj.comment
  }))
);
