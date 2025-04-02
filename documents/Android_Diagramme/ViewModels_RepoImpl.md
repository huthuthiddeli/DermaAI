---
config:
  layout: elk
---
classDiagram
direction LR
	namespace ViewModels {
        class AccountinfoViewModel {
	        +setEmail(email: String)
	        +setPassword(password: String)
	        +setIsLoggedIn(isLoggedIn: Boolean)
	        +register(email: String, password: String, url: String)
	        +login(email: String, password: String, mfa: Boolean, url: String)
	        +createTestUser()
	        +checkHealth(model: HealthCheckResponse, url: String)
	        +startHealthCheck(model: HealthCheckResponse, url: String)
	        +getKey() : String
	        +getUser() : User?
        }
        class AdminViewModel {
	        +retrainAll(url: String, model: RetrainAll)
	        +retrain(url: String, model: Retrain)
	        +getModels(url: String)
	        +setCurrentUser()
	        +getCurrentUser() : User?
	        +getAllReports(url: String, model: ReportAll)
	        +getOneReport(url: String, model: Report)
        }
        class CameraViewModel {
	        +setModelIndex(index: Int)
	        +setFramework(frw: String)
	        +getModelIndex() : Int
	        +getFramework() : String
	        +getLastPath() : String
	        +getCurrentUser() : User
        }
        class GalleryViewModel {
	        +loadImages(filesDir: File?)
        }
        class LoginViewModel {
	        +setStayLoggedIn(stayLoggedIn: Boolean)
	        +getStayLoggedIn() : Boolean?
        }
        class PhotoViewModel {
	        +requestCameraPermission()
	        +resetCameraPermissionRequest()
	        +setCurrentImage(photoFile: File)
	        +setTmpImage(tmpImage: File)
	        +getModels(url: String)
        }
        class ResizeViewModel {
	        +sendImage(url: String, modelIndex: Int, trainerString: String, base64Image: String, lastPathOfImg: String?)
	        +savePrediction(url: String, model: PredictionImage)
	        +resizeImage(url: String, base64: String)
	        +getCurrentUser() : User
        }
        class ResultViewModel {
	        +getCurrentUser() : User
        }
        class SettingsViewModel {
	        +syncImages(url: String)
	        +setMfa(url: String)
	        +generate2faKey(context: Context) : String
	        +validate2faCode(secret: String, code: String) : Boolean
	        +getCurrentUser() : User
        }
	}
	namespace Repositories {
        class AdminRepoImpl {
	        +retrainModel(model: Retrain?, url: String) : Result
	        +retrainAllModel(model: RetrainAll, url: String) : Result
	        +getOneReport(model: Report, url: String) : Result
	        +getAllReports(model: ReportAll, url: String) : Result
        }
        class ImageRepoImpl {
	        +sendImage(model: AiModel, url: String) : Result
	        +resizeImage(url: String, base64: String) : Result
	        +savePrediction(model: PredictionImage, url: String) : Result
	        +loadPredictions(model: User, url: String) : Result
        }
        class LoginRepoImpl {
	        +login(email: String, password: String, mfa: Boolean, url: String) : Result
	        +register(email: String, password: String, mfa: Boolean, url: String) : Result
	        +setMFA(user: User?, url: String) : Result
	        +checkHealth(model: HealthCheckResponse, url: String) : Result
        }
        class ModelRepoImpl {
	        +getModels(url: String) : Result
        }
        class UserRepoImpl {
	        +getCurrentUser() : User?
	        +saveCurrentUser(user: User)
        }
	}
    AccountinfoViewModel --> LoginRepoImpl
    AccountinfoViewModel --> UserRepoImpl
    AdminViewModel --> AdminRepoImpl
    AdminViewModel --> UserRepoImpl
    AdminViewModel --> ModelRepoImpl
    CameraViewModel --> ImageRepoImpl
    CameraViewModel --> UserRepoImpl
    GalleryViewModel --> UserRepoImpl
    PhotoViewModel --> ModelRepoImpl
    ResizeViewModel --> ImageRepoImpl
    ResizeViewModel --> UserRepoImpl
    ResultViewModel --> UserRepoImpl
    SettingsViewModel --> UserRepoImpl
    SettingsViewModel --> LoginRepoImpl
    SettingsViewModel --> ImageRepoImpl
