import sys

import requests
from bs4 import BeautifulSoup
import re
import time
import base64
from Picture import Picture
import json


# https://api.isic-archive.com/api/docs/swagger/#/lesions/isic_ingest_api_lesion_list
# TODO:  HAM10000 Dataset PH2 Dataset Derm7pt Dataset DermNet NZMED-NODE Dataset


def fetch_html(url, headers=None):
    # Send a GET request to the webpage
    requests.head = headers

    response = requests.get(url)

    # Check if the request was successful
    if response.status_code == 200:
        # Parse the page content
        return BeautifulSoup(response.content, 'html.parser')
    else:
        print(f"Failed to retrieve the webpage. Status code: {response.status_code}", file=sys.stderr)


def read_isic_archives():
    with open('links.txt', 'r') as file:
        strings = ' '.join(file.readlines())
        pattern = r'"url":\s*"([^"]+)"'
        matches = re.findall(pattern, strings)
        return matches


def download_isic_archives_pictures(links: list):
    byteList = []

    for link in links:
        response = requests.get(link)

        if response.status_code == 200:
            picturebase64 = base64.b64encode(response.content)
            pic = Picture(picturebase64)
            byteList.append(pic)

    return byteList


def to_json(json_string):
    return json.loads(str(json_string))


def find_regex(pattern, string):
    return re.search(pattern, string)


if __name__ == '__main__':
    connection_strings = [
        "https://dermnetnz.org/images",
        'https://api.isic-archive.com/api/v2/lesions/',
        "https://www.nature.com/articles/s41597-023-02630-0",
        'https://gallery.isic-archive.com/#!/topWithHeader/onlyHeaderTop/gallery?filter=%5B%5D',
        'https://content.isic-archive.com/922e5c9e-6c71-479…ZI-iWTWYDhSDwtpsfcag__&Key-Pair-Id=K1C8I6SNK7JVJ8'
    ]

    start_time = time.perf_counter()
    fetched_data = fetch_html(connection_strings[1])

    data = to_json(fetched_data)
    print(f'Type: {type(data["results"])}')

    content = data['results']

    aclist = []

    outcome_diagnosis_regex = r'\'outcome_diagnosis\':\s*\'([^\']+)'
    picture_regex = r'url\':\s*\'([^\']+)\''

    for i in content:
        diagnosis = find_regex(outcome_diagnosis_regex, str(i)).group(1)
        picture = find_regex(picture_regex, str(i)).group(1)

        if len(diagnosis) < 0 or len(picture) < 0:
            print("Iteration skipped!")
            continue

        print(picture)
        print(diagnosis)
        #base64string = download_isic_archives_pictures(picture)
        #aclist.append((diagnosis, base64string))

    for i in aclist:
        print(i)

    # list = read_isic_archives()
    # print("Items collected: ", len(list))
    # download_isic_archives_pictures(list)
    end_time = time.perf_counter()
    elapsed_time = end_time - start_time
    print(f"Elapsed time: {elapsed_time:.1f} seconds")
