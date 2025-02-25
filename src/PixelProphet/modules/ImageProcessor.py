import json
import PIL
import cv2
import numpy as np
import requests
from sklearn.preprocessing import StandardScaler


def loadImagesAs1DVectorFromJson(json_path, reshape_size):
    with open(json_path, 'r') as file:
        data = json.load(file)

    features_list = []
    labels_list = []

    for item in data:
        picture = np.array(item["picture"], dtype=np.uint8)
        label = item["diagnosis"]

        if len(picture.shape) == 2:  # Already grayscale
            gray_image = picture
        elif len(picture.shape) == 3:  # Convert only if it's RGB or RGBA
            gray_image = cv2.cvtColor(picture, cv2.COLOR_BGR2GRAY)
        else:
            raise ValueError(f"Unexpected image shape: {picture.shape}")

        # resize image
        resized_image = cv2.resize(gray_image, (reshape_size, reshape_size))

        # scale image as 1D vector
        features = resized_image.flatten()
        features_scaled = StandardScaler().fit_transform(features.reshape(-1, 1)).flatten()

        # append labels
        features_list.append(features_scaled)
        labels_list.append(label)

    return np.array(features_list), np.array(labels_list)


def loadImagesAs1DVectorFromAPI(api_uri, reshape_size):
    # fetch data from api
    data = __fetchDataFromAPI(api_uri)

    if not data:
        raise ValueError("Could not fetch from database")

    features_list = []
    labels_list = []

    for item in data:
        # load json
        picture = np.array(item["picture"], dtype=np.uint8)
        label = item["diagnosis"]

        if len(picture.shape) == 2:  # Already grayscale
            gray_image = picture
        elif len(picture.shape) == 3:  # Convert only if it's RGB or RGBA
            gray_image = cv2.cvtColor(picture, cv2.COLOR_BGR2GRAY)
        else:
            raise ValueError(f"Unexpected image shape: {picture.shape}")
        resized_image = cv2.resize(gray_image, (reshape_size, reshape_size))

        # scale image as 1D vectorre
        features = resized_image.flatten()
        features_scaled = StandardScaler().fit_transform(features.reshape(-1, 1)).flatten()

        # append labels
        features_list.append(features_scaled)
        labels_list.append(label)

    return np.array(features_list), np.array(labels_list)


def __fetchDataFromAPI(api_uri):
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


def preprocess_image(image, reshape_size):
    image = image.convert("L")
    image = image.resize((reshape_size, reshape_size), resample=PIL.Image.BICUBIC)
    image_array = np.array(image)
    image_array = image_array.reshape(1, -1)
    return image_array
