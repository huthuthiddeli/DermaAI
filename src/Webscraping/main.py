import os
import requests
from bs4 import BeautifulSoup
import re
import time
import picture as Picture
import json
import sys
import subprocess

# https://api.isic-archive.com/api/docs/swagger/#/lesions/isic_ingest_api_lesion_list
# TODO:  HAM10000 Dataset PH2 Dataset Derm7pt Dataset DermNet NZMED-NODE Dataset

outcome_diagnosis_regex_ENG_ISIC = r'\'outcome_diagnosis\':\s*\'([^\']+)'
pic_regex_ENG_ISIC = r"'full':\s*{\s*'url':\s*'([^']+)'"
next_ENG_ISIC = r"'next':\s*'([^']+)','"

gathered_data = []


def fetch_html(url):
    # Send a GET request to the webpage
    response = requests.get(url)

    # Check if the request was successful
    if response.status_code == 200:
        # Parse the page content
        # print(response.content)
        return BeautifulSoup(response.content, 'html.parser')
    else:
        print(f"Failed to retrieve the webpage. Status code: {response.status_code}", file=sys.stderr)


def read_isic_archives():
    with open('links.txt', 'r') as file:
        strings = ' '.join(file.readlines())
        pattern = r'"url":\s*"([^"]+)"'
        matches = re.findall(pattern, strings)
        return matches


def to_json(json_string):
    return json.loads(str(json_string))


def find_regex(pattern, string):
    string = string.replace("amp;", "")
    return re.search(pattern, string)


def convert_data(gathered_data):
    return [repr(item) for item in gathered_data]


def fetch_from_isic_archive(link):
        """Function which handles all important things, such like gathering, serialising data and finally returning them."""
        fetched_data = fetch_html(link)
        data = to_json(fetched_data)['results']
        next_link = to_json(fetched_data)['next']
        content = convert_data(data)
        finished_list = []

        for i in content:

            try:
                diagnosis = find_regex(outcome_diagnosis_regex_ENG_ISIC, str(i)).group(1)
                picture = find_regex(pic_regex_ENG_ISIC, str(i)).group(1)

                if diagnosis is None or picture is None:
                    continue

                if len(diagnosis) < 0 or len(picture) < 0:
                    print("Iteration skipped!")
                    continue

                print(f'Diagnosis: {diagnosis}')
                print(f'Pictures: {picture}')
                # print("-----------------")

                download_images(Picture.Picture(picture, diagnosis))


            except AttributeError as e:
                print(f'AttributeError: {e}')

            except Exception as e:
                print(f'Citical Error: {e}')

        if len(next_link) > 0:
            fetch_from_isic_archive(next_link)


def download_images(obj: Picture.Picture):
    result = subprocess.run([sys.executable, 'DownloadImage.py', str(obj.to_dict())],
                            cwd=os.getcwd(),
                            text=True,
                            capture_output=True)

    print(f'STDOUT: {result.stdout}\nSTDERR: {result.stderr}\n--------------------------------------------')


if __name__ == '__main__':
    connection_strings = [
        'https://api.isic-archive.com/api/v2/lesions/',  # code completed
    ]

    start_time = time.perf_counter()
    fetch_from_isic_archive(connection_strings[0])
    end_time = time.perf_counter()
    elapsed_time = end_time - start_time
    print(f"Elapsed time: {elapsed_time:.1f} seconds")
