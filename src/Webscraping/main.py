import os
import requests
from bs4 import BeautifulSoup
import re
import time
import picture as Picture
import json
import sys
import subprocess

outcome_diagnosis_regex_ENG_ISIC = r'\'outcome_diagnosis\':\s*\'([^\']+)'
pic_regex_ENG_ISIC = r"'full':\s*{\s*'url':\s*'([^']+)'"
next_ENG_ISIC = r"'next':\s*'([^']+)','"
connection_string = 'https://api.isic-archive.com/api/v2/lesions/'


def fetch_html(url):
    response = requests.get(url)
    if response.status_code == 200:
        return BeautifulSoup(response.content, 'html.parser')
    else:
        print(f"Failed to retrieve the webpage. Status code: {response.status_code}", file=sys.stderr)


def to_json(json_string):
    return json.loads(str(json_string))


def find_regex(pattern, string):
    string = string.replace("amp;", "")
    return re.search(pattern, string)


def convert_data(gathered_data):
    return [repr(item) for item in gathered_data]


def fetch_from_isic_archive(link):
    fetched_data = fetch_html(link)
    data = to_json(fetched_data)['results']
    next_link = to_json(fetched_data)['next']
    content = convert_data(data)

    for i in content:
        try:
            diagnosis = find_regex(outcome_diagnosis_regex_ENG_ISIC, str(i)).group(1)
            picture = find_regex(pic_regex_ENG_ISIC, str(i)).group(1)

            if diagnosis is None or picture is None:
                print("Iteration skipped!")
                continue
            if len(diagnosis) < 0 or len(picture) < 0:
                print("Iteration skipped!")
                continue
            download_images(Picture.Picture(picture, diagnosis))

        except Exception as e:
            print(f'Critical Error: {e}')

    if len(next_link) > 0:
        fetch_from_isic_archive(next_link)

def download_images(obj: Picture.Picture):
    result = subprocess.run([sys.executable, 'DownloadImage.py', str(obj.to_dict())],
                            cwd=os.getcwd(), capture_output=True)

    if len(result.stdout) > 5:
        print(f"STANDARD OUTPUT={result.stdout}")
    if len(result.stderr) > 5:
        print(f"STANDARD ERROR={result.stderr}")


if __name__ == '__main__':
    start_time = time.perf_counter()
    fetch_from_isic_archive(connection_string)
    end_time = time.perf_counter()
    elapsed_time = end_time - start_time
    print(f"Elapsed time: {elapsed_time: .1f} seconds\n")
