import json
import os
import PIL
from PIL import Image as PILImage
import cv2
import numpy as np
import requests
from matplotlib import pyplot as plt
from sklearn.preprocessing import StandardScaler


class ImageProcessor:
    def __init__(self, reshape_size):
        self.reshape_size = reshape_size

    def loadImagesAs1DVector(self, folder_path):
        features_list = []
        filenames_list = []

        for filename in os.listdir(folder_path):
            image_path = os.path.join(folder_path, filename)

            if os.path.isfile(image_path):
                image = cv2.imread(image_path)

                if image is not None:
                    gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
                    resized_image = cv2.resize(gray_image, (self.reshape_size, self.reshape_size))

                    # convert image to 1D vector
                    features = resized_image.flatten()

                    # rescale the image
                    features_scaled = StandardScaler().fit_transform(features.reshape(-1, 1)).flatten()

                    # append scaled features to list
                    features_list.append(features_scaled)

                    filename = os.path.splitext(os.path.basename(image_path))[0]
                    filenames_list.append(filename)
                else:
                    raise ValueError('Images could not be loaded')
        return np.array(features_list), np.array(filenames_list)

    def loadImageAs1DVector(self, image_path):
        if os.path.isfile(image_path):
            image = cv2.imread(image_path)

            if image is not None:
                gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

                resized_image = cv2.resize(gray_image, (self.reshape_size, self.reshape_size))

                features = resized_image.flatten()

                features_scaled = StandardScaler().fit_transform(features.reshape(-1, 1)).flatten()

                filename = os.path.splitext(os.path.basename(image_path))[0]

                return (features_scaled, filename)
            else:
                raise ValueError('Image could not be loaded')
        else:
            raise FileNotFoundError(f"File not found: {image_path}")

    def loadImagesAs1DVectorFromJson(self, json_path):
        # load json file
        with open(json_path, 'r') as file:
            data = json.load(file)

        features_list = []
        labels_list = []

        for item in data:
            picture = np.array(item["picture"], dtype=np.uint8)
            label = item["diagnosis"]

            # preprocess image
            gray_image = cv2.cvtColor(picture, cv2.COLOR_BGR2GRAY)
            resized_image = cv2.resize(gray_image, (self.reshape_size, self.reshape_size))

            # scale image as 1D vector
            features = resized_image.flatten()
            features_scaled = StandardScaler().fit_transform(features.reshape(-1, 1)).flatten()

            # append labels
            features_list.append(features_scaled)
            labels_list.append(label)

        return np.array(features_list), np.array(labels_list)

    def loadImagesAs1DVectorFromAPI(self, api_uri):
        # fetch data from api
        data = self.__fetchDataFromAPI(api_uri)

        if not data:
            raise ValueError("Could not fetch from database")

        features_list = []
        labels_list = []

        for item in data:
            # load json
            picture = np.array(item["picture"], dtype=np.uint8)
            label = item["diagnosis"]

            # preprocess image
            # Ensure image is at least 2D
            if len(picture.shape) == 2:  # Already grayscale
                gray_image = picture
            elif len(picture.shape) == 3:  # Convert only if it's RGB or RGBA
                gray_image = cv2.cvtColor(picture, cv2.COLOR_BGR2GRAY)
            else:
                raise ValueError(f"Unexpected image shape: {picture.shape}")
            resized_image = cv2.resize(gray_image, (self.reshape_size, self.reshape_size))

            # scale image as 1D vector
            features = resized_image.flatten()
            features_scaled = StandardScaler().fit_transform(features.reshape(-1, 1)).flatten()

            # append labels
            features_list.append(features_scaled)
            labels_list.append(label)

        return np.array(features_list), np.array(labels_list)

    def __fetchDataFromAPI(self, api_uri):
        print('\n# ----------- FETCHING DATA FROM DATABASE ----------- #\n')
        all_data = []
        page = 1

        try:
            while True:
                response = requests.get(f"{api_uri}?page={page}&limit=5")
                response.raise_for_status()
                data = response.json()

                # Append the current page's data
                all_data.extend(data.get("docs", []))

                # Check if there's a next page
                if not data.get("hasNextPage", False):
                    break

                # Move to the next page
                print("Loaded Page ", page)
                page += 1

            print('Received data with length of: ', all_data.__len__())
            return all_data

        except requests.exceptions.HTTPError as errh:
            print(f"HTTP-Error: {errh}")
        except requests.exceptions.ConnectionError as errc:
            print(f"Connection-Error: {errc}")
        except requests.exceptions.Timeout as errt:
            print(f"Timeout-Error: {errt}")
        except requests.exceptions.RequestException as err:
            print(f"Error: {err}")

        return None

    @staticmethod
    def preprocess_image(image: PILImage.Image, reshape_size):
        image = image.convert("L")
        image = image.resize((reshape_size, reshape_size), resample=PIL.Image.BICUBIC)
        image_array = np.array(image)

        # show preprocessed image
        #plt.imshow(image_array, cmap='gray')
        #plt.title("Preprocessed Image")
        #plt.show()

        image_array = image_array.reshape(1, -1)
        return image_array
