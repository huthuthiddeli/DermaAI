from sklearn.svm import SVC
from .training_basemodel import BaseModel


class SVM(BaseModel):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(SVC(kernel='linear'), reshape_size, model_save_path, "linear")