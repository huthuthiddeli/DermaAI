from .training_basemodel import BaseModelTF
from tensorflow import keras
from tensorflow.keras.layers import Conv2D, MaxPooling2D, GlobalAveragePooling2D, Dense, BatchNormalization, Dropout, Input


class CNNModel(BaseModelTF):
    def __init__(self, model_save_path, classes):
        self.num_classes = classes
        super().__init__(self.__build_model(200), self.__build_model, model_save_path, model_name_extension="_cnn")

    def __build_model(self, reshape_size):
        model = keras.Sequential([
            Input(shape=(reshape_size, reshape_size, 1)),
            Conv2D(32, (3, 3), activation='relu', kernel_initializer='he_normal', padding='same'),
            BatchNormalization(),
            MaxPooling2D((2, 2)),
            Conv2D(64, (3, 3), activation='relu', kernel_initializer='he_normal', padding='same'),
            BatchNormalization(),
            MaxPooling2D((2, 2)),
            Conv2D(128, (3, 3), activation='relu', kernel_initializer='he_normal', padding='same'),
            BatchNormalization(),
            MaxPooling2D((2, 2)),
            GlobalAveragePooling2D(),
            Dense(128, activation='relu', kernel_initializer='he_normal'),
            BatchNormalization(),
            Dropout(0.3),
            Dense(self.num_classes, activation='softmax')
        ])
        optimizer = keras.optimizers.AdamW(learning_rate=0.001)
        model.compile(optimizer=optimizer,
                      loss='sparse_categorical_crossentropy',
                      metrics=['accuracy'])
        return model
