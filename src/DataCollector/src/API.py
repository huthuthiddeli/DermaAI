from fastapi import FastAPI, File, UploadFile, status
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from find_spot import real_crop_image_microservice
from typing import List
from main import main
import uvicorn

app = FastAPI()

class ImageRequest(BaseModel):
    image: List[int]  # Accept image as a list of bytes (integers)

@app.post("/")
async def find_spot(request: ImageRequest):
    image_bytes = bytes(request.image)  # Convert list of integers back to bytes
    img = real_crop_image_microservice(image_bytes)  # Process image

    if img is None: 
        return {"error": "Processing failed"}

    return {"file": img.tolist()}

@app.post("/seed")
async def fill_db(status_code=status.HTTP_201_CREATED):
    main()
    return {"message": "Items retrieved!"}


if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=6969)
