from datetime import datetime

from fastapi import APIRouter, HTTPException, UploadFile, File
from fastapi.responses import Response
from starlette.responses import JSONResponse
import requests

from api.RequestModels import UserRequest, SavePredictionRequest


class UserController:
    def __init__(self, api_base_url):
        self.router = APIRouter()
        self.api_base_url = api_base_url

        @self.router.post("/create-user/")
        def create_user(user_data: UserRequest):
            user_data = user_data.dict()
            try:
                res = requests.post(f"{api_base_url[0]}/user/saveUser", json=user_data)
                return Response(content=res.content, media_type=res.headers["Content-Type"],
                                status_code=res.status_code)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

        @self.router.post("/login/")
        def login_user(user_data: UserRequest):
            user_data = user_data.dict()
            try:
                res = requests.post(f"{api_base_url[0]}/user/validateUser/", json=user_data)
                return Response(content=res.content, media_type=res.headers["Content-Type"],
                                status_code=res.status_code)

            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

        @self.router.post("/switch-mfa/")
        async def switch_mfa(request: UserRequest):
            request = request.dict()
            try:
                res = requests.post(f"{api_base_url[0]}/user/switchMfa", json=request)
                return Response(content=res.content, media_type=res.headers["Content-Type"],
                                status_code=res.status_code)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

        @self.router.post("/save-prediction/")
        async def save_prediction(request: SavePredictionRequest):
            request = request.dict()
            try:
                #timestamp = datetime.now().isoformat()
                #request["timestamp"] = timestamp
                res = requests.post(f"{api_base_url[0]}/prediction/savePrediction", json=request)
                return Response(content=res.content, media_type=res.headers["Content-Type"],
                                status_code=res.status_code)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")

        @self.router.post("/load-prediction/")
        async def load_predictions(request: UserRequest):
            request = request.dict()
            try:
                res = requests.post(f"{api_base_url[0]}/prediction/loadPrediction", json=request)
                return Response(content=res.content, media_type=res.headers["Content-Type"],
                                status_code=res.status_code)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")
