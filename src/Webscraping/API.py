from fastapi import FastAPI, UploadFile
from pydantic import BaseModel
from fastapi.responses import JSONResponse
from fastapi import Request
import uvicorn
from find_spot import crop_image_microservice, test_find_picture

# FastAPI instance
app = FastAPI()


class requestBody(BaseModel):
    image: bytes


# @app.post("/")
# async def getPic(file: UploadFile):
#     bytes_arr = await file.read()
#     img = crop_image_microservice(bytes_arr)
#     # img = test_find_picture(bytes_arr)
#     if img is None:
#         return JSONResponse(content={"file": "error!"})
#     return JSONResponse(content={"file": img.tolist()})

@app.post("/")
async def find_spot(request: requestBody):
    img = crop_image_microservice(request)

    if img is None:
        return

    return img

if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=6969)
