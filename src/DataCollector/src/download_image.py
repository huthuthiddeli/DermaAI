from dataclasses import dataclass, field
from typing import Optional
import sys
import requests
import json
from dataclasses import dataclass
from typing import Optional, Dict
import io
from io import BytesIO
import numpy as np
import cv2


assert len(sys.argv) > 1, "No arguments given!"

# PRODUCTION LINKG
URL = "http://node-app:3333/picture/picture"

# DEV LINK
# URL = "http://localhost:3333/picture/picture"
REQ_URL_IMAGE = "https://api.isic-archive.com/api/v2/images/"

##########################
#      STATUS CODES      #
##########################
# 3 - Statuscode not 200 #
##########################

class Entry:
    lesion_id: str
    image_id: str
    dx: str
    dx_type: str
    age: str
    sex: str
    localization: str
    dataset: str

@dataclass
class FileData:
    url: Optional[str] = None
    size: Optional[int] = None

@dataclass
class Files:
    full: Optional[FileData] = None
    thumbnail_256: Optional[FileData] = None

@dataclass
class Acquisition:
    pixels_x: Optional[int] = None
    pixels_y: Optional[int] = None
    image_type: Optional[str] = None

@dataclass
class Clinical:
    concomitant_biopsy: Optional[bool] = None
    sex: Optional[str] = None
    anatom_site_general: Optional[str] = None
    benign_malignant: Optional[str] = None
    diagnosis_1: Optional[str] = None
    diagnosis_2: Optional[str] = None
    diagnosis_3: Optional[str] = None
    diagnosis_confirm_type: Optional[str] = None
    melanocytic: Optional[bool] = None
    age_approx: Optional[int] = None
    lesion_id: Optional[str] = None

@dataclass
class Metadata:
    acquisition: Optional[Acquisition] = None
    clinical: Optional[Clinical] = None

@dataclass
class ImageEntry:
    isic_id: Optional[str] = None
    copyright_license: Optional[str] = None
    attribution: Optional[str] = None
    files: Optional[Files] = None
    metadata: Optional[Metadata] = None
    public: Optional[bool] = None


# Deserialization function
def parse_image_entry(json_str: str) -> ImageEntry:
    data = json.loads(json_str)

    # Parse nested parts manually
    files = Files(
        full=FileData(**data['files']['full']),
        thumbnail_256=FileData(**data['files']['thumbnail_256'])
    )

    acquisition = Acquisition(**data['metadata']['acquisition'])
    clinical = Clinical(**data['metadata']['clinical'])
    metadata = Metadata(acquisition=acquisition, clinical=clinical)

    return ImageEntry(
        isic_id=data['isic_id'],
        copyright_license=data['copyright_license'],
        attribution=data['attribution'],
        files=files,
        metadata=metadata,
        public=data['public']
    )


def get_image(url: str) -> io.BytesIO:
    response = requests.get(url)
    if response.status_code == 200:
        return io.BytesIO(response.content)
    else:
        sys.exit(3)

def save_numpy_as_image_cv(array: np.ndarray, path: str):
    """
    Save a NumPy array as an image using OpenCV.
    """
    print(f'Dimensions={array.shape[:2]}')
    success = cv2.imwrite(path, array)
    if not success:
        raise Exception(f"Failed to save image to {path}")


def download_image(url: str, output_path: str):
    response = requests.get(url, stream=True)
    if response.status_code == 200:
        with open(output_path, 'wb') as f:
            for chunk in response.iter_content(1024):
                f.write(chunk)
        print(f"Image saved to {output_path}")
    else:
        print(f"Failed to download image. Status code: {response.status_code}")


def parse_obj(item: str) -> Entry:
    return json.loads(s=item)

obj = parse_obj(str(sys.argv[1]))

image_res = requests.get(REQ_URL_IMAGE + obj.get("image_id"))
image_res.raise_for_status()
entry = parse_image_entry(image_res.content)
image_data = list(requests.get(entry.files.full.url).content)

data = {
    "picture": np.array(image_data, dtype=np.uint8).tolist(),
    "diagnosis": obj.get("dx")
}

print(data)

file = {
    "file": ("data.json", BytesIO(json.dumps(data).encode("utf-8")), "application/json")
}

response = requests.post(URL, files=file)
print(f'Response={response.status_code}, Body={response.text}')