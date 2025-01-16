import os
import cv2
import sys
from multiprocessing import shared_memory
import ast
import pickle
import picture as Picture


def find_path(obj: Picture.Picture) -> str:
    count = len(os.listdir("./cut_spots"))
    '''REMOVES ANY WHITESPACES FOR FILENAME AND : '''
    no_whitespaces = obj.get_diagnosis().replace(" ", "_").replace(":", "__")
    added_count = no_whitespaces + '___ITEM___' + str(count)
    filepath = os.getcwd() + "/cut_spots/" + added_count + ".jpg"
    return filepath

# byte array



# Region of Interest
def define_ROI(image):
    ROI_area = [100, 200, 300, 400]
    height, width = image.shape[:2]
    center_x, center_y = width // 2, height // 2
    roi_size = 100  # Adjust this value to set the size of the central region
    roi_x1, roi_y1 = center_x - roi_size, center_y - roi_size
    roi_x2, roi_y2 = center_x + roi_size, center_y + roi_size
    center_region = image[roi_y1:roi_y2, roi_x1:roi_x2]
    return center_region


def crop_image(array_shape: tuple, mem_name: str):
    array_shape = ast.literal_eval(array_shape)
    shm = shared_memory.SharedMemory(name=mem_name)
    serialized_data = bytes(shm.buf[:])  # Copy the data into a bytes object
    my_object = pickle.loads(serialized_data)
    image = my_object.get_picture()

    # REPLACE THIS STUFF

    height, width = image.shape[:2]
    center_x, center_y = width // 2, height // 2
    roi_size = 100  # Adjust this value to set the size of the central region
    roi_x1, roi_y1 = center_x - roi_size, center_y - roi_size
    roi_x2, roi_y2 = center_x + roi_size, center_y + roi_size
    center_region = image[roi_y1:roi_y2, roi_x1:roi_x2]

    # center_region = define_ROI(image)
    gray = cv2.cvtColor(center_region, cv2.COLOR_BGR2GRAY)
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)
    _, thresholded = cv2.threshold(blurred, 127, 255, cv2.THRESH_BINARY_INV)
    contours, _ = cv2.findContours(thresholded, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    min_area = 500  # Adjust based on the size of small anomalies to ignore
    output_image = image.copy()
    for idx, contour in enumerate(contours):
        if cv2.contourArea(contour) > min_area:  # Only consider large enough contours
            # Offset contour coordinates to match the original image
            contour += [roi_x1, roi_y1]
            cv2.drawContours(output_image, [contour], -1, (0, 255, 0), 2)
            x, y, w, h = cv2.boundingRect(contour)
            detected_spot = cv2.cvtColor(image[y:y + h, x:x + w], cv2.COLOR_BGR2GRAY)
            # Save the cropped spot as a separate file
            filepath = find_path(my_object)
            print(f'FILEPATH: {filepath}')
            cv2.imwrite(filepath, detected_spot)

    # Optional: Draw the ROI rectangle for reference
    cv2.rectangle(output_image, (roi_x1, roi_y1), (roi_x2, roi_y2), (255, 0, 0), 2)

    # Clean up shared memory when done
    shm.close()
    shm.unlink()


if __name__ == '__main__':
    assert len(sys.argv) > 2, "Not enough arguments given!"
    print(sys.argv)
    array_shape = sys.argv[1]
    mem_name = sys.argv[2]

    crop_image(array_shape, mem_name)
