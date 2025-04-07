---
config:
  theme: default
---
classDiagram
    class Storage {
        +base64ToBitmap(base64String: String): Bitmap?
        +retrieveImagesFromStorage(filesDir: File?, username: String): MutableList<File>
        +saveFileToStorage(bitmap: Bitmap, context: Context, filePath: String)
        +saveDiagnosis(activity: Activity, diagnosis: Diagnosis, username: String)
        +readAllDiagnoses(activity: Activity, username: String): List<Diagnosis>
        +readDiagnosisForImage(activity: Activity, imagePath: String, username: String): Map<String, Double>?
        +createReportFile(activity: Activity, report: String, username: String)
        +createUniqueImagePath(activity: Activity, username: String): File
        -getSubDir(username: String): String
        +convertImageToBase64(imageFile: File): String?
    }