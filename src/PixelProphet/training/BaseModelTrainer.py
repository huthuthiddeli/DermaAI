class ModelTrainer:
    def __init__(self, models, trainer_name):
        self.trainer_name = trainer_name
        self.models = models

    def get_all_models(self):
        return [model.model_name for model in self.models]

    def make_prediction(self, model_int, image_array):
        print(f'\n{self.trainer_name}\n# ----------- STARTING PREDICTION OF IMAGE ----------- #\nTrainer: {self.__class__.__name__}\nModel: {model_int}\n')
        model = self.__get_model(model_int)
        prediction = model.make_prediction(image_array)
        print('Finished Prediction\n')
        return prediction

    def train_model(self, model_int, dataset, epochs, reshape_size):
        error = ''
        model = self.__get_model(model_int)

        print(f'\n{self.trainer_name}\n# ----------- STARTING TRAINING OF {model.model_name} ----------- #\n')
        res = model.train(dataset, epochs, reshape_size)
        if res[0] is False:
            error = res[1]
        print(f'{error}\n')
        return error

    def train_all_models(self, dataset, epochs, reshape_size, batch_size=32, lr=0.001):
        errors = []
        print(f'\n{self.trainer_name}\n# ----------- STARTING TRAINING OF {len(self.models)} MODELS ----------- #\n')
        x = 1
        for model in self.models:
            print(f'{x}. {model.model_name}')
            res = model.train(dataset, epochs, reshape_size, batch_size=batch_size, lr=lr)
            if res[0] is False:
                errors.append(res[1])
                print(f'{errors[len(errors)-1]}')
            print('\n')
            x += 1
        print('\n')
        return errors

    def get_classification_report(self, model_int, dataset, reshape_size):
        report = ''
        error = ''
        model = self.__get_model(model_int)
        print(f'\n{self.trainer_name}\n# ----------- CLASSIFIER REPORT OF {model.model_name}  ----------- #\n')
        res = model.get_classification_report(dataset, reshape_size)
        if res[0] is False:
            error = res[1]
        else:
            report = res[1]
        print(f'{res[1]}\n')
        return report, error

    def get_all_classification_reports(self, dataset, reshape_size):
        print(f'\n{self.trainer_name}\n# ----------- CLASSIFIER REPORTS OF {len(self.models)} MODELS ----------- #\n')
        reports = {}
        errors = []

        # Iterate over all models and generate reports
        x = 1
        for model in self.models:
            print(f'{x}. {model.model_name}')
            res = model.get_classification_report(dataset, reshape_size)
            if res[0] is False:
                errors.append(res[1])
            else:
                reports[model.model_name] = res[1]
            print(f'{res[1]}\n')
            x += 1
        return reports, errors

    def __get_model(self, model_int):
        try:
            return self.models[model_int]
        except IndexError:
            raise NotImplementedError("Model not found")
