from fastapi import FastAPI, UploadFile, File, HTTPException

app = FastAPI()

# Endpunkt zum Auswerten eines Bildes
@app.post("/predict/")
async def predict_image(file: UploadFile = File(...)):
    # Bild in ein passendes Format umwandeln
    image = PILImage.open(io.BytesIO(await file.read()))
    image = image.resize((224, 224))
    image_array = np.array(image).reshape(1, -1)

    # Vorhersage machen
    prediction = model.predict(image_array)

    return {"prediction": prediction.tolist()}