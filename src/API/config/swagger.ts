import path from "node:path";
import url from "node:url";

export default {
  path: path.dirname(url.fileURLToPath(import.meta.url)) + "/../", // for AdonisJS v6
  tagIndex: 1,
  info: {
    title: "DermaAI API",
    version: "0.1.0",
    description: "Documentation of DermaAI",
  },
  snakeCase: true,

  debug: true, // set to true, to get some useful debug output
  ignore: ["/swagger", "/docs", "/"],
  
  preferredPutPatch: "PUT", // if PUT/PATCH are provided for the same route, prefer PUT
  common: {
    parameters: {
      // sortable: [
      //   {
      //     in: "query",
      //     name: "sortBy",
      //     schema: { type: "string", example: "foo" },
      //   },
      //   {
      //     in: "query",
      //     name: "sortType",
      //     schema: { type: "string", example: "ASC" },
      //   },
      // ],
    },
    
    headers: {
      // paginated: {
      //   "X-Total-Pages": {
      //     description: "Total amount of pages",
      //     schema: { type: "integer", example: 5 },
      //   },
      //   "X-Total": {
      //     description: "Total amount of results",
      //     schema: { type: "integer", example: 100 },
      //   },
      //   "X-Per-Page": {
      //     description: "Results per page",
      //     schema: { type: "integer", example: 20 },
      //   },
      // },
    },
  },
  securitySchemes: {}, // optional
  authMiddlewares: ["auth", "auth:api"], // optional
  defaultSecurityScheme: "BearerAuth", // optional
  persistAuthorization: true, // persist authorization between reloads on the swagger page
  showFullPath: false, // the path displayed after endpoint summary
};