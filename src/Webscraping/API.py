from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import uvicorn
import numpy as np
from find_spot import crop_image_from_bytearray

app = FastAPI()


class Picture(BaseModel):
    picture: bytearray

    class Config:
        arbitrary_types_allowed = True

class Response(BaseModel):
    picture: np.array


@app.get("/{picture}", response_model=Response)
def crop_img(picture: bytearray):
    return crop_image_from_bytearray(picture)
    raise HTTPException(status_code=404, detail="Error")


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=6969)
