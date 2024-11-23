import requests
import sys
import json
import os
import cv2
from io import BytesIO
import time

url = 'http://localhost:3333/picture'


def standart_routine():
    try:
        data = str(sys.argv[1])
        data = data.replace("'", '"')
        print(data)

        item = json.loads(data)

        response = requests.post(url, json=item)

        print(response.status_code)

    except Exception as e:
        print(f'Exception caught: {e}')
        sys.exit(1)


def decode_filenames(name: str) -> str:
    """READS FILENAME AND GETS DIAGNOSIS FROM IT"""
    finish = name.replace("__", ":")
    white_spaces = finish.replace("_", " ")
    items = white_spaces.split("ITEM")[0].strip()

    if items[-1] == ':':
        items = items[:-1]

    return items


def local_files():
    try:
        # Path to the directory
        directory = "./cut_spots/"

        # List all files in the directory with their names
        files_with_names = [(file, os.path.join(directory, file)) for file in os.listdir(directory) if
                            os.path.isfile(os.path.join(directory, file))]

        # Print file names and paths
        for file_name, file_path in files_with_names:
            diagnosis = decode_filenames(file_name)

            image = cv2.imread(file_path)

            data = {
                "picture": image.tolist(),
                "diagnosis": diagnosis
            }

            serialized_data = json.dumps(data)

            file_like_obj = BytesIO(serialized_data.encode('utf-8'))

            filename = "data.json"

            file = {"file": (filename, file_like_obj, "application/json")}

            response = requests.post(url, files=file)

            print(f'\033[31mUPLOADING DATA TO DB STATUSCODE: {response.status_code}\n'
                  f'Response:{len(response.content)}\n\033[0m')
            # print(f"File Name: {file_name}, File Path: {file_path}")
            # sys.exit(-1)
    except ConnectionError as Cerr:
        print(f'CONNECTIONERROR OCCURED: {Cerr}', file=sys.stderr)


if __name__ == '__main__':
    # assert len(sys.argv) > 1, "Not enough arguments given!"
    start_time = time.perf_counter()

    local_files()

    end_time = time.perf_counter()
    elapsed_time = end_time - start_time
    print(f'Upload took: {elapsed_time:.1f} seconds')
