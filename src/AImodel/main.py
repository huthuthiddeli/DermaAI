import joblib
from matplotlib import pyplot as plt
from images import ImageProcessor
from frontend_api import APIHandler
from training.training import ModelTrainer

# ----------- CONFIGURATION ----------- #
PATH_TO_MODEL = 'trained_models/trained_knnc.joblib'
MODEL_SAVE_PATH = 'trained_models/'
PATH_TO_TESTDATA = 'test/IMAGES.images.json'
RESHAPE_SIZE = 128
DB_API_URI = 'http://192.168.110.29:3333/getAllPictures'


# ----------- START APPLICATION ----------- #
def main():
    # FETCH DATASET FROM DATABASE
    print('\n# ----------- FETCHING DATA FROM DATABASE ----------- #\n')
    dataset = ImageProcessor(DB_API_URI, RESHAPE_SIZE).loadImagesAs1DVectorFromJson(PATH_TO_TESTDATA)
    print('Received data with length of: ', dataset[0].__len__())

    # CREATE MODEL TRAINER WITH EXISTING DATASET
    model_trainer = ModelTrainer(dataset, RESHAPE_SIZE, MODEL_SAVE_PATH)

    # TRAIN ALL MODELS
    print('\n# ----------- STARTING TRAINING ----------- #\n')
    #model_trainer.train_all_models()

    # SHOW TEST PREDICTIONS FOR TRAINED MODELS
    print('\n# ----------- STARTING PREDICTIONS ----------- #\n')
    model_trainer.show_prediction()

    # START API FOR FRONTEND
    print('\n# ----------- STARTING APPLICATION ----------- #\n')
    #APIHandler(PATH_TO_MODEL, RESHAPE_SIZE).start()


if __name__ == "__main__":
    main()
