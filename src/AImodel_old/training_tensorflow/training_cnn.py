from training_tensorflow.training_basemodel import BaseModelTF
from tensorflow import keras
from tensorflow.keras.layers import Conv2D, MaxPooling2D, GlobalAveragePooling2D, Dense, BatchNormalization, Dropout, Input

class CNNModel(BaseModelTF):
    def __init__(self, reshape_size, model_save_path):
        model = keras.Sequential([
            Input(shape=(reshape_size, reshape_size, 1)),  # Statt input_shape in Conv2D

            # Erste Convolutional-Schicht
            Conv2D(32, (3, 3), activation='relu', kernel_initializer='he_normal', padding='same'),
            BatchNormalization(),
            MaxPooling2D((2, 2)),

            # Zweite Convolutional-Schicht
            Conv2D(64, (3, 3), activation='relu', kernel_initializer='he_normal', padding='same'),
            BatchNormalization(),
            MaxPooling2D((2, 2)),

            # Dritte Convolutional-Schicht
            Conv2D(128, (3, 3), activation='relu', kernel_initializer='he_normal', padding='same'),
            BatchNormalization(),
            MaxPooling2D((2, 2)),

            # GlobalAveragePooling statt Flatten für kompaktere Features
            GlobalAveragePooling2D(),

            # Vollständig verbundene Schicht mit Dropout
            Dense(128, activation='relu', kernel_initializer='he_normal'),
            BatchNormalization(),
            Dropout(0.3),

            # Ausgabeschicht für 10 Klassen
            Dense(10, activation='softmax')
        ])

        # Model-Optimierung mit AdamW (besser als Adam bei Regularisierung)
        model.compile(optimizer=keras.optimizers.AdamW(learning_rate=0.001),
                      loss='sparse_categorical_crossentropy',
                      metrics=['accuracy'])

        super().__init__(model, reshape_size, model_save_path)
