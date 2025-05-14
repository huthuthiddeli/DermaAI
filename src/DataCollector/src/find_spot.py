import cv2
import numpy as np

def crop_image_microservice(image: bytes):
    # Decode image from byte array
    bimage = np.frombuffer(image, dtype=np.uint8)
    image = cv2.imdecode(bimage, cv2.IMREAD_COLOR)

    # Get image dimensions
    height, width = image.shape[:2]
    center_x, center_y = width // 2, height // 2

    # Initialize previous detected spot and pixel counts
    prev_detected_spot = np.zeros((0, 0, 0), dtype=np.uint8)
    latest_big = np.zeros((0, 0, 0), dtype=np.uint8)
    iterations = 0
    max_iterations = 300
    min_area = 10  # Minimum contour area to consider

    # Loop for iterative processing of the image
    while iterations < max_iterations:
        try:
            roi_size = 25 + (iterations * 100)  # Increment ROI size each iteration
            roi_x1, roi_y1 = center_x - roi_size, center_y - roi_size
            roi_x2, roi_y2 = center_x + roi_size, center_y + roi_size
            roi = image[roi_y1:roi_y2, roi_x1:roi_x2]

            if roi.shape[0] == 0 or roi.shape[1] == 0:
                print("ROI size exceeds image dimensions, breaking loop.")
                break

            # Process region of interest (ROI)
            center_region = process_roi(roi)

            # Find contours in the thresholded image
            contours, _ = cv2.findContours(center_region, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

            print(f"CONTOURS AMMOUNT={len(contours)}")

            for idx, contour in enumerate(contours):
                if cv2.contourArea(contour) > min_area:  # Filter small contours

                    x, y, w, h = cv2.boundingRect(contour)
                    x += roi_x1
                    y += roi_y1
                    detected_spot = cv2.cvtColor(image[y:y + h, x:x + w], cv2.COLOR_BGR2GRAY)

                    # Find the biggest spot
                    if prev_detected_spot.size < detected_spot.size:
                        prev_detected_spot = detected_spot

            if latest_big.size == prev_detected_spot.size and prev_detected_spot.size != 0:
                print(f"Returning last detected spot after {iterations} iterations.")
                # DEBUGGING
                # cv2.imshow("window", latest_big)
                # cv2.waitKey(0)
                # cv2.destroyAllWindows()

                return latest_big
            elif latest_big.size < prev_detected_spot.size:
                latest_big = prev_detected_spot

            iterations += 1
        except Exception as e:
            print(f"Error={e}")
            return latest_big
    print(f"Returning last detected spot after {iterations} iterations.")
    return latest_big


def process_roi(roi):
    """
    Process the ROI (Region of Interest) by converting to grayscale,
    applying Gaussian Blur, and thresholding.
    """
    try:
        # TODO: INCREASE / DECREASE CONTRAST IF BAD
        gray = cv2.cvtColor(roi, cv2.COLOR_BGR2GRAY)
        # qualized = cv2.equalizeHist(gray)
        blurred = cv2.GaussianBlur(gray, (5, 5), 0)
        edges = cv2.Canny(blurred, 50, 150)
        _, thresholded = cv2.threshold(edges, 127, 255, cv2.THRESH_BINARY_INV)
        return thresholded
    except:
        print("error")


def test_find_picture(image: bytes):
    bimage = np.frombuffer(image, dtype=np.uint8)
    image = cv2.imdecode(bimage, cv2.IMREAD_COLOR)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Apply Gaussian Blur
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)

    # Edge detection using Canny
    edges = cv2.Canny(blurred, 50, 150)

    # Find contours
    contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    if not contours:
        return None  # No contours found

        # Find the largest contour by area
    largest_contour = max(contours, key=cv2.contourArea)
    # Draw contours on the original image
    cv2.drawContours(image, contours, -1, (0, 255, 0), 2)

    # Show result
    cv2.imshow("big", largest_contour)
    cv2.imshow('Contours', image)
    cv2.waitKey(0)
    cv2.destroyAllWindows()

    return largest_contour

import logging

# Configure logging for better debugging
logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

def real_crop_image_microservice(image: bytes):
    """
    Detects and extracts a skin lesion from the center region of the image using iterative ROI expansion.
    """
    # Decode image from byte array
    bimage = np.frombuffer(image, dtype=np.uint8)
    image = cv2.imdecode(bimage, cv2.IMREAD_COLOR)

    if image is None:
        logging.error("Failed to decode image.")
        return None

    height, width = image.shape[:2]
    center_x, center_y = width // 2, height // 2

    prev_detected_spot = None  # Store the largest detected lesion
    latest_big = None
    iterations = 0
    max_iterations = 20  # Reduce iterations to improve efficiency
    min_area = 50  # Ignore very small contours
    max_roi_size = min(height, width) // 2  # Prevent ROI from exceeding image bounds

    # Iteratively expand the ROI and search for lesions
    while iterations < max_iterations:
        try:
            roi_size = min(25 + (iterations * 50), max_roi_size)  # Adaptive ROI size
            roi_x1, roi_y1 = max(center_x - roi_size, 0), max(center_y - roi_size, 0)
            roi_x2, roi_y2 = min(center_x + roi_size, width), min(center_y + roi_size, height)
            roi = image[roi_y1:roi_y2, roi_x1:roi_x2]

            if roi.size == 0:
                logging.warning("ROI exceeds image dimensions. Stopping iterations.")
                break

            # Process region of interest (ROI) and find contours
            center_region = process_roi(roi)
            contours, _ = cv2.findContours(center_region, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

            logging.info(f"Iteration {iterations}: Found {len(contours)} contours.")

            for contour in contours:
                if cv2.contourArea(contour) > min_area:
                    x, y, w, h = cv2.boundingRect(contour)
                    x, y = x + roi_x1, y + roi_y1  # Adjust coordinates to original image
                    detected_spot = image[y:y + h, x:x + w]

                    # Store the largest lesion detected
                    if prev_detected_spot is None or detected_spot.size > prev_detected_spot.size:
                        prev_detected_spot = detected_spot

            # If no larger lesion is found, return the previous one
            if latest_big is not None and prev_detected_spot is not None and prev_detected_spot.size == latest_big.size:
                logging.info(f"Returning detected lesion after {iterations} iterations.")
                return latest_big
            elif prev_detected_spot is not None and (latest_big is None or prev_detected_spot.size > latest_big.size):
                latest_big = prev_detected_spot

            iterations += 1

        except Exception as e:
            logging.error(f"Unexpected error: {e}")
            return latest_big

    logging.info(f"Returning last detected lesion after {iterations} iterations.")
    return latest_big

def process_roi(roi):
    """
    Enhances contrast, applies edge detection, and thresholding to improve lesion detection.
    """
    try:
        gray = cv2.cvtColor(roi, cv2.COLOR_BGR2GRAY)

        # Improve contrast using CLAHE (Contrast Limited Adaptive Histogram Equalization)
        clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8, 8))
        enhanced = clahe.apply(gray)

        # Apply Gaussian blur for noise reduction
        blurred = cv2.GaussianBlur(enhanced, (5, 5), 0)

        # Use adaptive thresholding instead of fixed threshold
        thresholded = cv2.adaptiveThreshold(blurred, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                            cv2.THRESH_BINARY_INV, 11, 2)

        # Apply morphological operations to close small gaps
        kernel = np.ones((3, 3), np.uint8)
        morphed = cv2.morphologyEx(thresholded, cv2.MORPH_CLOSE, kernel, iterations=2)

        return morphed
    except Exception as e:
        logging.error(f"Error in processing ROI: {e}")
        return np.zeros(roi.shape[:2], dtype=np.uint8)  # Return a blank image in case of failure
