from training_tensorflow.training_basemodel import BaseModelTF
from tensorflow import keras


class ResNetModel(BaseModelTF):
    def __init__(self, reshape_size, model_save_path):
        base_model = keras.applications.ResNet50(weights=None, input_shape=(reshape_size, reshape_size, 3), classes=10)
        base_model.compile(optimizer='adam', loss='sparse_categorical_crossentropy', metrics=['accuracy'])
        super().__init__(base_model, reshape_size, model_save_path)
