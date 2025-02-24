from ImageProcessor import ImageProcessor

from training_pytorch.ModelTrainerPyTorch import ModelTrainerPyTorch
from training_sklearn.ModelTrainerSKLearn import ModelTrainerSKLearn
from training_tensorflow.ModelTrainerTensorFlow import ModelTrainerTensorFlow


class ModelTrainer:
    def __init__(self, reshape_size, model_save_path, api_base_url, num_classes):
        self.reshape_size = reshape_size
        self.model_save_path = model_save_path
        self.apir_base_url = api_base_url
        self.images_processor = ImageProcessor(reshape_size)
        self.dataset = None

        # Initialize all trainers
        self.trainers = [
            ModelTrainerPyTorch(reshape_size, model_save_path, num_classes),
            ModelTrainerSKLearn(reshape_size, model_save_path),
            ModelTrainerTensorFlow(reshape_size, model_save_path)
        ]

    # return the list of all available models
    def get_all_models(self):
        # Create a dictionary to hold trainer names and their respective models
        all_models = {}

        for trainer in self.trainers:
            trainer_name = type(trainer).__name__
            model_names = trainer.get_all_models()  # Get the models for each trainer
            all_models[trainer_name] = model_names

        return all_models

    # Make prediction for provided image
    def make_prediction(self, trainer_string, model_int, image_array):
        if trainer_string == 'ModelTrainerPyTorch':
            return self.trainers[0].make_prediction(model_int, image_array)
        elif trainer_string == 'ModelTrainerSKLearn':
            return self.trainers[1].make_prediction(model_int, image_array)
        elif trainer_string == 'ModelTrainerTensorFlow':
            return self.trainers[2].make_prediction(model_int, image_array)
        else:
            raise ValueError('Invalid trainer')

    def train_model(self, trainer_string, model_int):
        # Select the trainer based on trainer_string
        if trainer_string == 'ModelTrainerPyTorch':
            trainer = self.trainers[0]
        elif trainer_string == 'ModelTrainerSKLearn':
            trainer = self.trainers[1]
        elif trainer_string == 'ModelTrainerTensorFlow':
            trainer = self.trainers[2]
        else:
            raise ValueError('Invalid trainer')

        # Fetch the newest available data
        dataset = self.__fetch_dataset()
        trainer.train(model_int, dataset)  # Assuming the trainer has a train method that takes the model and dataset

        print("Training completed successfully")

    # Train all models
    def train_all_models(self):
        # fetch newest available data
        dataset = self.__fetch_dataset()

        # iterate over all models and train them
        for trainer in self.trainers:
            trainer.train_all_models(dataset)

        print("Training completed successfully")

    # Return a specific classifier report
    def get_classifier_report(self, trainer_string, model_int):
        # Select the trainer based on trainer_string
        if trainer_string == 'ModelTrainerPyTorch':
            trainer = self.trainers[0]
        elif trainer_string == 'ModelTrainerSKLearn':
            trainer = self.trainers[1]
        elif trainer_string == 'ModelTrainerTensorFlow':
            trainer = self.trainers[2]
        else:
            raise ValueError('Invalid trainer')

        # Fetch the newest available data
        dataset = self.__fetch_dataset()
        return trainer.get_classification_report(model_int, dataset)

    def get_all_classification_reports(self):
        reports = {}

        # Fetch the newest available dataset
        dataset = self.__fetch_dataset()

        # Iterate over all trainers and get their classification reports
        for trainer in self.trainers:
            try:
                trainer_name = trainer.__class__.__name__  # Get the trainer's class name
                reports[trainer_name] = trainer.get_all_classification_reports(dataset)
            except Exception as e:
                reports[trainer_name] = f"Error generating report: {str(e)}"

        return reports

    def __fetch_dataset(self):
        #dataset = self.images_processor.loadImagesAs1DVectorFromAPI(self.apir_base_url + "/picture/picture")
        dataset = self.images_processor.loadImagesAs1DVectorFromJson('test/IMAGES.images.json')
        return dataset
