import os
import sys
import requests
import cv2
import subprocess
import numpy as np
import json
import pickle
from multiprocessing import shared_memory
import picture as Picture

# check if link has been provided
assert len(sys.argv) > 1, "Not enough arguments given!"

try:
    data = str(sys.argv[1])
    data = data.replace("'", '"')
    item = json.loads(data)

    response = requests.get(item['picture'])

    if response.status_code == 200:
        np_array = np.frombuffer(response.content, np.uint8)
        image = cv2.imdecode(np_array, cv2.IMREAD_COLOR)

        picture = Picture.Picture(image, item['diagnosis'])

        serialized_obj = pickle.dumps(picture)

        # Create shared memory
        shm = shared_memory.SharedMemory(create=True, size=len(serialized_obj))

        # Write the serialized data to shared memory
        shm.buf[:len(serialized_obj)] = serialized_obj

        print(f"Shared memory name: {shm.name}")

        array_shape = str(image.shape)

        subprocess.run([sys.executable, 'find_spot.py', array_shape, shm.name], cwd=os.getcwd())

    else:
        print("ERROR", file=sys.stderr)

except Exception as e:
    print(e)