from sklearn.naive_bayes import GaussianNB
from .training_basemodel import BaseModel


class NaiveBayes(BaseModel):
    def __init__(self, model_save_path):
        super().__init__(GaussianNB(), model_save_path)