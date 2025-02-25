from sklearn.svm import SVC
from .training_basemodel import BaseModel


class RBF_SVM(BaseModel):
    def __init__(self, model_save_path):
        super().__init__(SVC(kernel='rbf'), model_save_path, "_rbf")
