from .training_basemodel import BaseModelTF
from tensorflow import keras


class DenseNetModel(BaseModelTF):
    def __init__(self, model_save_path, classes):
        self.num_classes =  classes
        super().__init__(self.__build_model(200), self.__build_model, model_save_path)

    def __build_model(self, reshape_size):
        model = keras.applications.DenseNet121(
            weights=None,
            input_shape=(reshape_size, reshape_size, 1),
            classes=self.num_classes
        )
        optimizer = keras.optimizers.AdamW(learning_rate=0.001)
        model.compile(optimizer=optimizer,
                      loss='sparse_categorical_crossentropy',
                      metrics=['accuracy'])
        return model
