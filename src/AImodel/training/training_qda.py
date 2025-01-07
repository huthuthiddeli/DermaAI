from sklearn.discriminant_analysis import QuadraticDiscriminantAnalysis
from training.training_basemodel import BaseModel


class QDA(BaseModel):
    def __init__(self, dataset, reshape_size, model_save_path):
        super().__init__(QuadraticDiscriminantAnalysis(reg_param=0.1), dataset, reshape_size, model_save_path)
