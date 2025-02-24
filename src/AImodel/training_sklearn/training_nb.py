from sklearn.naive_bayes import GaussianNB
from .training_basemodel import BaseModel


class NaiveBayes(BaseModel):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(GaussianNB(), reshape_size, model_save_path)