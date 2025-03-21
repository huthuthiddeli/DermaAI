import requests

from fastapi import APIRouter, HTTPException
from fastapi.responses import Response
from api.RequestModels import ResizeRequest


class ImageController:
    def __init__(self, api_base_url):
        self.router = APIRouter()

        @self.router.post("/resize/")
        async def resize_image(request: ResizeRequest):
            request = request.dict()
            try:
                res = requests.post(api_base_url[1], json=request)
                return Response(content=res.content, media_type=res.headers["Content-Type"],
                                status_code=res.status_code)
            except Exception as e:
                raise HTTPException(status_code=500, detail=f"Error: {str(e)}")




