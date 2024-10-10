import cv2
from sklearn.preprocessing import StandardScaler

# Bild laden
image_path = 'hautlaesion.jpg'
image = cv2.imread(image_path)

# Bild in Graustufen konvertieren und Größe ändern (Optional)
gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
resized_image = cv2.resize(gray_image, (128, 128))

# Bild als Feature-Vektor (1D) umwandeln
features = resized_image.flatten()

# Feature-Skalierung (Optional)
scaler = StandardScaler()
features_scaled = scaler.fit_transform(features.reshape(-1, 1)).flatten()
