from fastapi import FastAPI, UploadFile
from fastapi.responses import JSONResponse
import uvicorn
from find_spot import crop_image_microservice

# FastAPI instance
app = FastAPI()

@app.get("/")
async def getPic(file: UploadFile):
    bytesArr = await file.read()
    img = crop_image_microservice(bytesArr)
    if img is None:
        return JSONResponse(content={"file": "error!"})
    return JSONResponse(content={"file": img.tolist()})


if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=6969)