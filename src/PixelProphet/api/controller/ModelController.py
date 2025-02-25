from datetime import datetime

from fastapi import APIRouter, HTTPException, UploadFile, File
import requests
from pydantic import BaseModel
from starlette.responses import JSONResponse
import base64
import io
from PIL import Image as PILImage

from api.RequestModels import ClassifierReportRequest, ClassifierReportsRequest, PredictionRequest, RetrainAllRequest, \
    RetrainRequest


class ModelController:
    def __init__(self, api_base_url, model_trainer):
        self.router = APIRouter()
        self.model_trainer = model_trainer

        # Get all models
        @self.router.get("/models")
        async def get_all_models():
            try:
                models = self.model_trainer.get_all_models()
                return JSONResponse(content=models, status_code=200)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error while fetching models: {str(e)}")

        # Get a specific classifier report
        @self.router.get("/classifier-report/")
        def get_classifier_report(request: ClassifierReportRequest):
            request = request.dict()
            try:
                payload = {
                    "email": request["email"],
                    "password": request["password"]
                }
                #res = __validate_user(payload)
                res = requests.post(api_base_url[0] + '/user/validateUser', json=payload)
                if res.status_code != 200:
                    raise HTTPException(status_code=res.status_code, detail=res.text)

                res_json = res.json()
                if res_json["isAdmin"] is True:
                    res = self.model_trainer.get_classifier_report(request["trainer_string"], request["model_int"],
                                                                   request["reshape_size"])
                else:
                    raise HTTPException(status_code=401, detail="Unauthorized")
                return JSONResponse(content={"report": res[0], "error": res[1]}, status_code=200)

            except NotImplementedError as nie:
                raise HTTPException(status_code=404, detail=f"Error: {str(nie)}")
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error while fetching report: {str(e)}")

        # Get all classifier reports
        @self.router.get("/classifier-reports/")
        def get_all_models(request: ClassifierReportsRequest):
            request = request.dict()
            try:
                payload = {
                    "email": request["email"],
                    "password": request["password"]
                }
                res = requests.post(api_base_url[0] + '/user/validateUser', json=payload)
                if res.status_code != 200:
                    raise HTTPException(status_code=res.status_code, detail=res.text)

                res_json = res.json()
                if res_json["isAdmin"] is True:
                    res = self.model_trainer.get_all_classifier_reports(request["reshape_size"])
                else:
                    raise HTTPException(status_code=401, detail="Unauthorized")
                return JSONResponse(content={"reports": res[0], "errors": res[1]}, status_code=200)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error while fetching reports: {str(e)}")

        # Make prediction
        @self.router.post("/predict/")
        async def predict_image(request: PredictionRequest):
            request = request.dict()
            try:
                image_data = base64.b64decode(request["image"])
                image = PILImage.open(io.BytesIO(image_data))
                prediction = self.model_trainer.make_prediction(request["trainer_string"], request["model_int"], image)

                return JSONResponse(content={
                    "trainer_string": request["trainer_string"],
                    "model_id": request["model_int"],
                    "prediction": prediction
                }, status_code=200)

            except NotImplementedError as nie:
                raise HTTPException(status_code=404, detail=f"Error: {str(nie)}")
            except Exception as e:
                print(str(e))
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

        # Retrain all models
        @self.router.post("/retrain-all/")
        async def retrain_models(request: RetrainAllRequest):
            request = request.dict()
            try:
                payload = {
                    "email": request["email"],
                    "password": request["password"]
                }
                res = requests.post(api_base_url[0] + '/user/validateUser', json=payload)
                if res.status_code != 200:
                    raise HTTPException(status_code=res.status_code, detail=res.text)

                res_json = res.json()
                if res_json["isAdmin"] is True:
                    errors = self.model_trainer.train_all_models(request["num_epochs"], request["reshape_size"])
                else:
                    raise HTTPException(status_code=401, detail="Unauthorized")
                return JSONResponse(content={"message": "Finished training", "errors": errors}, status_code=200)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

        # Retrain 1 model
        @self.router.post("/retrain/")
        def retrain_model(request: RetrainRequest):
            request = request.dict()
            try:
                payload = {
                    "email": request["email"],
                    "password": request["password"],
                }
                res = requests.post(api_base_url[0] + '/user/validateUser', json=payload)
                if res.status_code != 200:
                    raise HTTPException(status_code=res.status_code, detail=res.text)

                res_json = res.json()
                if res_json["isAdmin"] is True:
                    error = self.model_trainer.train_model(request["trainer_string"], request["model_int"],
                                                           request["num_epochs"], request["reshape_size"])
                else:
                    raise HTTPException(status_code=401, detail="Unauthorized")

                return JSONResponse(
                    content={"message": f"Finished training model with trainer {request['trainer_string']} and ID"
                                        f" {request['model_int']}", "error": error}, status_code=200)
            except NotImplementedError as nie:
                raise HTTPException(status_code=404, detail=f"Error: {str(nie)}")
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")
