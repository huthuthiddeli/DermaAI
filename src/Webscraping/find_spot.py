# import cv2
# import numpy as np
#
# # TODO: FLIP IMAGE TO OTHER SIDE AND CHECK AGAIN
#
#
# def crop_image_microservice(image: bytes):
#     # Decode image from byte array
#     bimage = np.frombuffer(image, dtype=np.uint8)
#     image = cv2.imdecode(bimage, cv2.IMREAD_COLOR)
#
#     if image is None:
#         print("Error: Image decoding failed.")
#         return None
#
#     # Get image dimensions
#     height, width = image.shape[:2]
#     center_x, center_y = width // 2, height // 2
#
#     # Initialize previous detected spot and pixel counts
#     prev_detected_spot = np.zeros((1, 1), dtype=np.uint8)
#     cached_biggest = np.zeros((1, 1), dtype=np.uint8)
#     cached_contours = 0
#     iterations = 0
#     max_iterations = 300
#     min_area = 10  # Minimum contour area to consider
#
#     # Loop for iterative processing of the image
#     while iterations < max_iterations:
#         try:
#             roi_size = 25 + (iterations * 100)  # Increment ROI size each iteration
#             roi_x1, roi_y1 = max(0, center_x - roi_size), max(0, center_y - roi_size)
#             roi_x2, roi_y2 = min(width, center_x + roi_size), min(height, center_y + roi_size)
#
#             roi = image[roi_y1:roi_y2, roi_x1:roi_x2]
#
#             if roi.size == 0:
#                 print("ROI size exceeds image dimensions, breaking loop.")
#                 break
#
#             # Process region of interest (ROI)
#             center_region = process_roi(roi)
#             if center_region is None:
#                 print("Error: ROI processing failed.")
#                 break
#
#             # Find contours in the thresholded image
#             contours, _ = cv2.findContours(center_region, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
#
#             print(f"CONTOURS AMOUNT={len(contours)}")
#
#             for contour in contours:
#                 if cv2.contourArea(contour) > min_area:  # Filter small contours
#                     x, y, w, h = cv2.boundingRect(contour)
#                     x += roi_x1
#                     y += roi_y1
#                     detected_spot = cv2.cvtColor(image[y:y + h, x:x + w], cv2.COLOR_BGR2GRAY)
#
#                     # Find the biggest spot
#                     if detected_spot.size > prev_detected_spot.size:
#                         prev_detected_spot = detected_spot
#
#             if cached_biggest.size == prev_detected_spot.size and prev_detected_spot.size != 0 or cached_contours == len(contour):
#                 print(f"Returning last detected spot after {iterations} iterations.")
#                 cv2.imwrite("test" + str(iterations) + ".png", cached_biggest)
#                 return cached_biggest
#             elif prev_detected_spot.size > cached_biggest.size:
#                 cached_biggest = prev_detected_spot
#
#             iterations += 1
#             cached_contours = len(contour)
#         except Exception as e:
#             print(f"Error: {e}")
#             return None
#
#     print(f"Returning last detected spot after {iterations} iterations.")
#     return cached_biggest
#
#
# def process_roi(roi):
#     """
#     Process the ROI (Region of Interest) by converting to grayscale,
#     applying Gaussian Blur, and thresholding.
#     """
#     try:
#         gray = cv2.cvtColor(roi, cv2.COLOR_BGR2GRAY)
#         blurred = cv2.GaussianBlur(gray, (5, 5), 0)
#         _, thresholded = cv2.threshold(blurred, 127, 255, cv2.THRESH_BINARY_INV)
#         return thresholded
#     except Exception as e:
#         print(f"Error caught: {e}")
#         return None  # Ensure function always returns something valid


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
                cv2.imshow("window", latest_big)
                cv2.waitKey(0)
                cv2.destroyAllWindows()

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
