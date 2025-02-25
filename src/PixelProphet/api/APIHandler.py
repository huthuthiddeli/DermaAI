import uvicorn
import requests
import logging
from datetime import datetime
from fastapi import FastAPI, Request
from starlette.responses import JSONResponse, HTMLResponse
from starlette.staticfiles import StaticFiles
from training.ModelTrainerContainer import ModelTrainerContainer
from .controller.ImageController import ImageController
from .controller.ModelController import ModelController
from .controller.UserController import UserController

# Logger konfigurieren
logging.basicConfig(
    filename="api_requests.log",  # Log-Datei
    level=logging.INFO,
    format="%(asctime)s - %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)


class APIHandler:
    def __init__(self, model_save_path, api_base_url):
        self.app = FastAPI(title="DermaAI", description="API for DermaAI", version="1.0.0")
        self.app.mount("/static", StaticFiles(directory="static"), name="static")

        labels = None
        try:
            response = requests.get(f"{api_base_url[0]}/picture/labels")
            if response.status_code == 200:
                data = response.json()
                diagnoses = data.get("diagnoses", [])
                labels = len(diagnoses)
                print("Length of diagnoses:", labels)
            else:
                print("Failed to fetch data:", response.status_code)
        except Exception:
            labels = 1
            print("Database API not reachable for labels")

        self.model_trainer = ModelTrainerContainer(model_save_path, api_base_url[0], labels)

        model_controller = ModelController(api_base_url, self.model_trainer)
        user_controller = UserController(api_base_url)
        image_controller = ImageController(api_base_url)

        self.app.include_router(model_controller.router, prefix="/model", tags=["Model"])
        self.app.include_router(user_controller.router, prefix="/user", tags=["User"])
        self.app.include_router(image_controller.router, prefix="/image", tags=["Image"])

        # **Middleware für Logging**
        @self.app.middleware("http")
        async def log_requests(request: Request, call_next):
            timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            log_message = f"[{timestamp}] {request.method} {request.url} FROM {request.client.host}"

            try:
                body = await request.json()
                log_message += f" | BODY: {body}"
            except Exception:
                log_message += " | BODY: No JSON body"

            logging.info(log_message)

            response = await call_next(request)
            return response

        # Health endpoint
        @self.app.get("/health/")
        def test():
            return JSONResponse(content={"message": "Application is live"}, status_code=200)

    def start(self):
        uvicorn.run(self.app, host="0.0.0.0", port=8000)
