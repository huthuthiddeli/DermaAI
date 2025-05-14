from dataclasses import dataclass
import csv
from typing import List
import subprocess
import os
import sys
import json


@dataclass
class Entry:
    lesion_id: str
    image_id: str
    dx: str
    dx_type: str
    age: str
    sex: str
    localization: str

def readFile() -> List[Entry]:
    entries = []  
    # with open('data.csv', newline='') as csvfile:
    with open("HAM10000_metadata.csv", newline='') as csvfile: 
        reader = csv.DictReader(csvfile)
        for row in reader:
            entry = Entry(
                lesion_id=row['lesion_id'],
                image_id=row['image_id'],
                dx=row['dx'],
                dx_type=row['dx_type'],
                age=(row['age']),
                sex=row['sex'],
                localization=row['localization']
            )
            entries.append(entry)
    csvfile.close()
    return entries

def download_image(entry:Entry):
    print(json.dumps(entry.__dict__))
    result = subprocess.run([sys.executable, './src/download_image.py', json.dumps(entry.__dict__)],
                            cwd=os.getcwd(), capture_output=True)

    print(result.stdout)
    print(result.stderr)

def main() -> None:
    entries = readFile()
    for entry in entries:
        download_image(entry=entry)
    print("Fetching finished!")

if __name__ == '__main__':
    main()
    
