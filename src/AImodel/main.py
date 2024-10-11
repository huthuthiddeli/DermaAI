import joblib
from matplotlib import pyplot as plt
from sklearn import datasets
from loadImages import loadImageAs1DVector

# ----------- test for number recognition -----------
def main(image_path):
    # load model
    model = joblib.load('trained_models/trained_knn.joblib')

    # prepare image
    features, filename = loadImageAs1DVector(image_path)

    # prediction
    prediction = model.predict(features)
    print("Classification for '", filename, "':", prediction[0])

if __name__ == "__main__":
    image_path = 'test_images/sechs_generated.png'
    main(image_path)
