from fastapi import FastAPI, UploadFile, File, HTTPException, requests
from PIL import Image as PILImage
from fastapi.params import Form
from starlette.responses import JSONResponse

from ImageProcessor import ImageProcessor
import io
import uvicorn

from ModelTrainer import ModelTrainer


class APIHandler:
    def __init__(self, reshape_size, model_save_path, api_base_url):
        self.app = FastAPI()
        self.model_trainer = ModelTrainer(reshape_size, model_save_path, api_base_url, 13)

        # Health endpoint
        @self.app.get("/health/")
        async def test():
            return JSONResponse(content={"message": "Application is live"}, status_code=200)

        # Get all models
        @self.app.get("/models/")
        async def get_all_models():
            try:
                models = self.model_trainer.get_all_models()
                return JSONResponse(content=models, status_code=200)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error while fetching models: {str(e)}")
            return

        # Get a specific classifier report
        @self.app.get("/classifier-report/")
        async def get_classifier_report(trainer_string: str = Form(...), model_int: int = Form(...)):
            try:
                report = self.model_trainer.get_classifier_report(trainer_string, model_int)
                return JSONResponse(content=report, status_code=200)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error while fetching report: {str(e)}")
            return

        # Get all classifier reports
        @self.app.get("/classifier-reports/")
        async def get_all_models():
            try:
                reports = self.model_trainer.get_all_classification_reports()
                return JSONResponse(content=reports, status_code=200)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error while fetching reports: {str(e)}")
            return

        # Make prediction
        @self.app.post("/predict/")
        async def predict_image(
                file: UploadFile = File(...),
                model_int: int = Form(...),
                trainer_string: str = Form(...)
        ):
            try:
                # Load image and preprocess it
                image = PILImage.open(io.BytesIO(await file.read()))
                image_array = ImageProcessor.preprocess_image(image, 128)

                # Make prediction using the specified trainer
                prediction = self.model_trainer.make_prediction(trainer_string, model_int, image_array)

                return JSONResponse(content={
                    "filename": file.filename,
                    "trainer": trainer_string,
                    "prediction": prediction
                }, status_code=200)

            except NotImplementedError as nie:
                raise HTTPException(status_code=404, detail=f"Error: {str(nie)}")
            except ValueError as ve:
                raise HTTPException(status_code=500, detail=f"Error: {str(ve)}")
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

        # Retrain all models
        @self.app.post("/retrain-all/")
        async def retrain_models():
            try:
                self.model_trainer.train_all_models()
                return JSONResponse(content={"message": "Successfully trained all models"}, status_code=200)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

        # Retrain 1 model
        @self.app.post("/retrain/")
        async def retrain_model(trainer_string: str = Form(...), model_int: int = Form(...)):
            try:
                self.model_trainer.train_model(trainer_string, model_int)

                return JSONResponse(content={"message": f"Successfully trained model with trainer {trainer_string} and ID {model_int}"}, status_code=200)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

        @self.app.post("/resize/")
        async def resize_image(file: UploadFile = File(...)):
            try:
                image_data = await file.read()

                response = requests.post(api_base_url+'/resize', files={"file": image_data})

                if response.status_code != 200:
                    raise HTTPException(status_code=response.status_code,
                                        detail="Error while trying to optimize the image.")

                return JSONResponse(content=response.content, media_type="image/jpeg")
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

    def start(self):
        uvicorn.run(self.app, host="0.0.0.0", port=8000)
