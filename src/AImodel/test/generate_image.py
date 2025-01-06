import numpy as np
import matplotlib.pyplot as plt
import cv2

# Erstelle ein leeres Bild (8x8) mit allen Werten auf 0 (schwarz)
digit_image = np.zeros((8, 8), dtype=np.uint8)

digit_image[1, 2:6] = 255  # obere horizontale Linie
digit_image[2, 6] = 255     # rechte vertikale Linie
digit_image[3, 2:6] = 255   # mittlere horizontale Linie
digit_image[4, 6] = 255     # rechte vertikale Linie
digit_image[5, 2:6] = 255   # untere horizontale Linie

# Zeige das Bild an
plt.imshow(digit_image, cmap='gray', vmin=0, vmax=255)
plt.title("Generated Image")
plt.axis('off')
plt.show()

# Speichere das Bild
cv2.imwrite("../test_images/drei_generated.png", digit_image)