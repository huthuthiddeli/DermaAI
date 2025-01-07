from fastapi import FastAPI, UploadFile, File, HTTPException
import joblib
from PIL import Image as PILImage
from images import ImageProcessor
import io


class APIHandler:
    def __init__(self, model_path, reshape_size):
        self.app = FastAPI()
        self.model = joblib.load(model_path)
        self.reshape_size = reshape_size

        @self.app.get("/test/")
        async def test():
            return {"message": "Application is live"}

        @self.app.post("/predict/")
        async def predict_image(file: UploadFile = File(...)):
            try:
                # load image and preprocess it
                image = PILImage.open(io.BytesIO(await file.read()))
                image_array = ImageProcessor.preprocess_image(image, reshape_size)

                # make prediction
                prediction = self.model.predict(image_array)

                return {"prediction": prediction.tolist()}
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

    def start(self):
        import uvicorn
        uvicorn.run(self.app, host="0.0.0.0", port=8000)
