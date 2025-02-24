from sklearn.discriminant_analysis import QuadraticDiscriminantAnalysis
from .training_basemodel import BaseModel


class QDA(BaseModel):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(QuadraticDiscriminantAnalysis(reg_param=0.1), reshape_size, model_save_path)
