import base64
import io
import os
import zlib
from tkinter import Image
import pandas as pd
import cv2
import numpy as np
import requests
from matplotlib import pyplot as plt
from sklearn.preprocessing import StandardScaler

api_url = "http://192.168.110.29:3333/getAllPictures"

def loadImagesAs1DVector(folder_path):
    # Initialize lists to hold the feature vectors and filenames
    features_list = []
    filenames_list = []

    # Iterate over all files in the specified folder
    for filename in os.listdir(folder_path):
        # Construct the full file path
        image_path = os.path.join(folder_path, filename)

        # Load image only if it's a file
        if os.path.isfile(image_path):
            # Load image
            image = cv2.imread(image_path)

            # Check if the image was loaded successfully
            if image is not None:
                # Convert image into gray scale and resize
                gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
                resized_image = cv2.resize(gray_image, (128, 128))

                # Convert image to 1-dimensional vector (16384,)
                features = resized_image.flatten()

                # Scale feature image (digits around 0 with deviation 1)
                scaler = StandardScaler()
                features_scaled = scaler.fit_transform(features.reshape(-1, 1)).flatten()

                # Append the scaled features to the list
                features_list.append(features_scaled)

                # Extract filename without extension
                filename = os.path.splitext(os.path.basename(image_path))[0]
                filenames_list.append(filename)

    # Convert the list to a NumPy array and return it
    return (np.array(features_list), np.array(filenames_list))

def loadImageAs1DVector(image_path):
    # Bild laden
    image = cv2.imread(image_path)

    if image is not None:
        # Bild in Graustufen umwandeln
        gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        # Bild auf 8x8 Pixel verkleinern
        resized_image = cv2.resize(gray_image, (8, 8))

        # Optional: Kontrast verbessern (Histogramm-Ausgleich)
        equalized_image = cv2.equalizeHist(resized_image)

        # Pixelwerte zwischen 0 und 16 normalisieren
        normalized_image = (equalized_image / 255.0) * 16

        # Bild als 1D-Vektor darstellen
        features = normalized_image.flatten()

        #reshape features
        features = features.reshape(1, -1)

        # Filename extrahieren
        filename = os.path.splitext(os.path.basename(image_path))[0]

        # Zeige das Bild
        plt.imshow(equalized_image, cmap='gray')
        plt.title("Preprocessed and Resized Image (8x8)")
        plt.show()

        # Feature-Vektor und Dateiname zurückgeben
        return features, filename
    else:
        raise ValueError("Image could not be loaded. Please check the file path.")

def loadImagesAs1DVectorFromAPI():
    """Konvertiert die Bilddaten (Base64) und Diagnosen in ein Dataset."""
    image_data = []
    labels = []

    data = __fetchDataFromAPI()

    for entry in data:
        # Füge die Base64-Daten zur Liste hinzu
        image_data.append(entry['picture'])
        labels.append(entry['diagnosis'])

        # Erstelle ein DataFrame aus den Listen
    dataset = pd.DataFrame({
        'picture': image_data,
        'diagnosis': labels
    })

    return dataset

def decode_images():
    for index, row in loadImagesAs1DVectorFromAPI().iterrows():
        # Hole die komprimierten Binärdaten des Bildes
        compressed_image_data = row['picture']

        with open("test.txt", "w") as file:
            file.write(compressed_image_data)

        print(compressed_image_data)
        # Dekomprimiere die Daten mit zlib
        decompressed_data = zlib.decompress(compressed_image_data)

        # Konvertiere die dekomprimierten Daten in ein Bild und speichere es
        image = Image.open(io.BytesIO(decompressed_data))
        image.save(f'image_{index}.png')

def __fetchDataFromAPI():
    try:
        response = requests.get(api_url)
        response.raise_for_status()
        data = response.json()

        return data

    except requests.exceptions.HTTPError as errh:
        print(f"HTTP-Error: {errh}")
        return None
    except requests.exceptions.ConnectionError as errc:
        print(f"Connection-Error: {errc}")
        return None
    except requests.exceptions.Timeout as errt:
        print(f"Timeout-Error: {errt}")
        return None
    except requests.exceptions.RequestException as err:
        print(f"Error: {err}")
        return None

decode_images()
