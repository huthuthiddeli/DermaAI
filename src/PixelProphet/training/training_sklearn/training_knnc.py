from sklearn.neighbors import KNeighborsClassifier
from .training_basemodel import BaseModel


class KNN(BaseModel):
    def __init__(self, model_save_path):
        super().__init__(KNeighborsClassifier(n_neighbors=1), model_save_path)
