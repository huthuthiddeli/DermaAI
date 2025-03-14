import sys
import requests
import numpy as np
import json
from io import BytesIO

# Check if link has been provided
assert len(sys.argv) > 1, "Not enough arguments given!"
url = "http://localhost:3333/picture"

try:
    data = str(sys.argv[1])
    data = data.replace("'", '"')
    item = json.loads(data)
    response = requests.get(item['picture'])

    if response.status_code == 200:
        image_data = list(response.content)  # Convert image bytes to a list of integers
        new_response = requests.post("http://localhost:6969/", json={"image": image_data})
        
        # Check response
        if new_response.status_code == 200:
            print("Response:", new_response.json())
        else:
            print("Error:", new_response.status_code, new_response.text)
            exit(-1)

        print(new_response.json())
        img = new_response.json()

        if img == 'error!' or np.array(img, dtype=np.uint8).size == 0:
            print("Error")
            exit(-1)

        data = {
            "picture": np.array(img, dtype=np.uint8).tolist(),
            "diagnosis": item['diagnosis']
        }

        file = {"file": ("data.json", BytesIO(json.dumps(data).encode("utf-8")), "application/json")}
        response = requests.post(url, files=file)

    else:
        print("Request was not successful", file=sys.stderr)

except Exception as e:
    print(f"General exception={e}")
