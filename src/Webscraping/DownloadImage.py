import os
import sys
import requests
import numpy as np
import json
from io import BytesIO

# check if link has been provided
assert len(sys.argv) > 1, "Not enough arguments given!"

url = "http://localhost:3333/picture"

try:
    data = str(sys.argv[1])
    data = data.replace("'", '"')
    item = json.loads(data)
    response = requests.get(item['picture'])

    if response.status_code == 200:
        with open("picture.png", "wb") as file:
            file.write(response.content)

        file = {'file': open("picture.png", "rb")}
        new_response = requests.get("http://localhost:6969/", files=file)
        img = new_response.json()["file"]

        if img == 'error!':
            exit(10)

        arr = np.array(img, dtype=np.uint8)

        if arr.size == 0:
            print("Error")
            exit(-1)

        llist = new_response.json()['file']
        np_arr = np.array(llist, dtype=np.uint8)

        data = {
            "picture": np_arr.tolist(),
            "diagnosis": item['diagnosis']
        }

        serialized_data = json.dumps(data).encode("utf-8")
        file_like_obj = BytesIO(serialized_data)
        filename = "data.json"
        file = {"file": (filename, file_like_obj, "application/json")}
        response = requests.post(url, files=file)
    else:
        print("ERROR", file=sys.stderr)

except Exception as e:
    print(f"General exception={e}")
