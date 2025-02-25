import requests
import json

def fetch_data_from_api(api_uri, output_file="IMAGES1.images.json"):
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
            if page == 3:
                break

            print(f"Loaded Page {page}")
            page += 1

        print(f'Received data with length of: {len(all_data)}')

        # Speichern in eine JSON-Datei
        with open(output_file, "w", encoding="utf-8") as f:
            json.dump(all_data, f, indent=4, ensure_ascii=False)

        print(f"Data saved to {output_file}")

    except requests.exceptions.HTTPError as errh:
        print(f"HTTP-Error: {errh}")
    except requests.exceptions.ConnectionError as errc:
        print(f"Connection-Error: {errc}")
    except requests.exceptions.Timeout as errt:
        print(f"Timeout-Error: {errt}")
    except requests.exceptions.RequestException as err:
        print(f"Error: {err}")

if __name__ == "__main__":
    API_URI = "http://93.111.12.119:3333/picture/picture"
    fetch_data_from_api(API_URI)
