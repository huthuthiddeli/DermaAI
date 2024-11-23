import os
import cv2
import sys
import numpy as np
from multiprocessing import shared_memory
import ast
import pickle
import picture as Picture


# def find_contours_whole(img):
#     _detected_spot = None
#     normalized_img = cv2.normalize(img, None, alpha=0, beta=255, norm_type=cv2.NORM_MINMAX)
#     # gray_image = cv2.cvtColor(normalized_img, cv2.COLOR_BGR2GRAY)
#     # blurred_image = cv2.GaussianBlur(gray_image, (5, 5), 0)
#
#     hsv_image = cv2.cvtColor(normalized_img, cv2.COLOR_BGR2HSV)
#     lower_bound = np.array([0, 0, 200])  # Example for bright spots
#     upper_bound = np.array([255, 255, 255])
#     mask = cv2.inRange(hsv_image, lower_bound, upper_bound)
#
#     kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (5, 5))
#     mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel)
#
#     height, width = mask.shape[:2]
#     margin = 0.1
#     margin_x, margin_y = int(width * margin), int(height * margin)
#
#     # Initialize a black mask, then draw a white rectangle in the central region
#     central_region_mask = np.zeros_like(mask, dtype=np.uint8)
#     cv2.rectangle(central_region_mask, (margin_x, margin_y), (width - margin_x, height - margin_y), 255, -1)
#
#     # Combine the masks to keep only contours in the central region
#
#     mask = cv2.bitwise_or(mask, central_region_mask)
#     cv2.imshow("mask", mask)
#     cv2.waitKey(0)
#     cv2.destroyAllWindows()
#
#     # hintergrund ändern
#     # kanten checken
#     # blob absorbieren
#
#     # Use thresholding to highlight areas that differ in intensity
#     _, thresholded = cv2.threshold(mask, 127, 255, cv2.THRESH_BINARY_INV)
#
#     # Find contours in the thresholded image within the central ROI
#     contours, _ = cv2.findContours(thresholded, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
#
#     min_area = 200  # Define a minimum area
#     contours = [c for c in contours if cv2.contourArea(c) > min_area]
#
#     _output_image = img.copy()
#     cv2.drawContours(_output_image, contours, -1, (0, 255, 0), 2)
#
#     return _output_image
#
#
# def crop_img_grayscale(img: cv2.Mat | np.ndarray):
#     _output_image = None
#     # normalized_img = cv2.normalize(img, None, alpha=0, beta=255, norm_type=cv2.NORM_MINMAX)
#     gray_image = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
#     blurred_image = cv2.GaussianBlur(gray_image, (5, 5), 0)
#
#     mask = np.zeros_like(blurred_image, dtype=np.uint8)
#     cv2.rectangle(mask, (50, 50), (img.shape[1] - 50, img.shape[0] - 50), (255, 255, 255), -1)
#
#     cv2.imshow("MASK", mask)
#
#     kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (5, 5))
#     mask = cv2.morphologyEx(blurred_image, cv2.MORPH_CLOSE, kernel)
#
#     cv2.imshow("mask", mask)
#
#     new_mask = cv2.bitwise_or(blurred_image, mask=mask)
#
#     cv2.imshow("imagggge", new_mask)
#
#     cv2.waitKey(0)
#     cv2.destroyAllWindows()
#
#     height, width = mask.shape[:2]
#     margin = 0.1
#     margin_x, margin_y = int(width * margin), int(height * margin)
#
#     central_region_mask = mask
#     cv2.rectangle(central_region_mask, (margin_x, margin_y), (width - margin_x, height - margin_y), 255, -1)
#
#     # Use thresholding to highlight areas that differ in intensity
#     _, thresholded = cv2.threshold(mask, 127, 255, cv2.THRESH_BINARY_INV)
#
#     # Find contours in the thresholded image within the central ROI
#     contours, _ = cv2.findContours(thresholded, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
#
#     min_area = 100  # Define a minimum area
#     contours = [c for c in contours if cv2.contourArea(c) > min_area]
#
#     _output_image = img.copy()
#     cv2.drawContours(_output_image, contours, -1, (0, 255, 0), 2)
#
#     # Use thresholding to highlight areas that differ in intensity
#     _, thresholded = cv2.threshold(mask, 127, 255, cv2.THRESH_BINARY_INV)
#
#     # Find contours in the thresholded image within the central ROI
#     contours, _ = cv2.findContours(thresholded, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
#
#     min_area = 200  # Define a minimum area
#     contours = [c for c in contours if cv2.contourArea(c) > min_area]
#
#     _output_image = img.copy()
#     cv2.drawContours(_output_image, contours, -1, (0, 255, 0), 2)
#
#     if _output_image is None:
#         print(f'Output image={_output_image}')
#         sys.exit(-1)
#
#     return _output_image

def find_path(obj: Picture.Picture) -> str:
    count = len(os.listdir("./cut_spots"))
    '''REMOVES ANY WHITESPACES FOR FILENAME AND : '''
    no_whitespaces = obj.get_diagnosis().replace(" ", "_").replace(":", "__")
    added_count = no_whitespaces + '___ITEM___' + str(count)
    filepath = os.getcwd() + "/cut_spots/" + added_count + ".jpg"
    return filepath


def crop_image(array_shape: tuple, mem_name: str):
    # Replace 'name_of_shared_memory' with the name printed by the first script
    # shm = shared_memory.SharedMemory(name=mem_name)

    array_shape = ast.literal_eval(array_shape)

    assert array_shape is not tuple, "Array_shape is not tuple"

    # Create a NumPy array backed by the shared memory
    array_dtype = np.uint8  # Replace with the dtype of the original array
    # shared_array = np.ndarray(array_shape, dtype=array_dtype, buffer=shm.buf)

    # Connect to existing shared memory
    shm = shared_memory.SharedMemory(name=mem_name)

    # Read the serialized data from shared memory
    serialized_data = bytes(shm.buf[:])  # Copy the data into a bytes object

    # Deserialize the object
    my_object = pickle.loads(serialized_data)


    # print(f'\033[31mShared Memory: {my_object.to_dict()}\033[0m')

    # Access the shared memory data
    # print(f'\033[31mShared Memory: {shared_array}\033[0m')

    image = my_object.get_picture()

    # Define the central region of interest (ROI)
    height, width = image.shape[:2]
    center_x, center_y = width // 2, height // 2
    roi_size = 100  # Adjust this value to set the size of the central region
    roi_x1, roi_y1 = center_x - roi_size, center_y - roi_size
    roi_x2, roi_y2 = center_x + roi_size, center_y + roi_size

    # Crop the ROI from the image for processing
    center_region = image[roi_y1:roi_y2, roi_x1:roi_x2]

    # normalized_img = cv2.normalize(center_region, None, alpha=0, beta=255, norm_type=cv2.NORM_MINMAX)
    # cv2.imshow("IMAGE", normalized_img)

    # Apply GaussianBlur to reduce noise
    gray = cv2.cvtColor(center_region, cv2.COLOR_BGR2GRAY)

    blurred = cv2.GaussianBlur(gray, (5, 5), 0)

    # Use thresholding to highlight areas that differ in intensity
    _, thresholded = cv2.threshold(blurred, 127, 255, cv2.THRESH_BINARY_INV)

    # Find contours in the thresholded image within the central ROI
    contours, _ = cv2.findContours(thresholded, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # Minimum area threshold for filtering small contours
    min_area = 500  # Adjust based on the size of small anomalies to ignore

    # Draw contours on the original image and save each highlighted spot
    output_image = image.copy()
    for idx, contour in enumerate(contours):
        if cv2.contourArea(contour) > min_area:  # Only consider large enough contours
            # Offset contour coordinates to match the original image
            contour += [roi_x1, roi_y1]
            cv2.drawContours(output_image, [contour], -1, (0, 255, 0), 2)

            # Get bounding box around the contour
            x, y, w, h = cv2.boundingRect(contour)

            # Crop the detected spot from the original image
            detected_spot = cv2.cvtColor(image[y:y + h, x:x + w], cv2.COLOR_BGR2GRAY)
            # cv2.imshow("fetected", detected_spot)
            # Save the cropped spot as a separate file
            filepath = find_path(my_object)
            print(f'FILEPATH: {filepath}')
            cv2.imwrite(filepath, detected_spot)
            # cv2.imwrite(filepath_default, image)

    # Optional: Draw the ROI rectangle for reference
    cv2.rectangle(output_image, (roi_x1, roi_y1), (roi_x2, roi_y2), (255, 0, 0), 2)

    # cv2.imshow("TEST", detected_spot)
    #
    # cv2.imshow("TEST3", output_image)
    # cv2.waitKey(0)
    # cv2.destroyAllWindows()

    # Clean up shared memory when done
    shm.close()
    shm.unlink()
    # return image, output_image, detected_spot


if __name__ == '__main__':
    assert len(sys.argv) > 2, "Not enough arguments given!"
    print(sys.argv)
    array_shape = sys.argv[1]
    mem_name = sys.argv[2]

    crop_image(array_shape, mem_name)
