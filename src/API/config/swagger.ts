import path from "node:path";
import url from "node:url";

export default {
  path: path.dirname(url.fileURLToPath(import.meta.url)) + "/../", // for AdonisJS v6
  tagIndex: 2,

  info: {
    title: "DermaAI API",
    version: "0.1.0",
    description: "Documentation of DermaAI",
  },
  snakeCase: true,

  debug: false, // set to true, to get some useful debug output
  ignore: ["/swagger", "/docs", "/"],

  preferredPutPatch: "PUT", // if PUT/PATCH are provided for the same route, prefer PUT
  securitySchemes: {}, // optional
  authMiddlewares: ["auth", "auth:api"], // optional
  defaultSecurityScheme: "BearerAuth", // optional
  persistAuthorization: true, // persist authorization between reloads on the swagger page
  showFullPath: false, // the path displayed after endpoint summary

  common: {
    headers: {},
    parameters: {},
  },
};