from api.APIHandler import APIHandler

# ----------- CONFIGURATION ----------- #
PATH_TO_MODEL = 'trained_models/trained_knn.joblib'
MODEL_SAVE_PATH = 'trained_models/'
PATH_TO_TESTDATA = 'test/IMAGES.images.json'
#DB_API_URI = ('http://10.10.1.193:3333', 'http://10.10.1.193:6969')
DB_API_URI = ('http://46.207.215.66:3333', 'http://46.207.215.66:6969')



# ----------- START APPLICATION ----------- #
def main():
    # FETCH DATASET FROM DATABASE
    #print('\n# ----------- FETCHING DATA FROM DATABASE ----------- #\n')
    #dataset = ImageProcessor(DB_API_URI).loadImagesAs1DVectorFromAPI()
    #print('Received data with length of: ', dataset[0].__len__())

    # CREATE MODEL TRAINER WITH EXISTING DATASET
    #model_trainer = ModelTrainer(dataset, RESHAPE_SIZE, MODEL_SAVE_PATH)

    # TRAIN ALL MODELS
    #print('\n# ----------- STARTING TRAINING OF ----------- #\n')
    #model_trainer.train_all_models()

    # SHOW TEST PREDICTIONS FOR TRAINED MODELS
    #print('\n# ----------- STARTING PREDICTIONS ----------- #\n')
    #model_trainer.show_prediction()
    # START API FOR FRONTEND
    print('\n# ----------- STARTING APPLICATION ----------- #\n')

    APIHandler(MODEL_SAVE_PATH, DB_API_URI).start()


if __name__ == "__main__":
    main()
