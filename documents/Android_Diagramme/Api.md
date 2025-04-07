---
config:
  theme: default
---
classDiagram
    class API {
        +callApi(apiUrl: String, httpMethod: String, requestModel: Any?): Result<String>
        -sendRequest(connection: HttpURLConnection, httpMethod: String, requestModel: Any?)
        -sendPost(connection: HttpURLConnection, requestModel: Any?)
        -readResponse(inputStream: InputStream): String
    }
