import os
import cv2
import numpy as np
from matplotlib import pyplot as plt
from sklearn import datasets
from sklearn.preprocessing import StandardScaler


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
