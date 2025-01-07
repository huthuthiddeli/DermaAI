from sklearn.naive_bayes import GaussianNB
from training.training_basemodel import BaseModel


class NaiveBayes(BaseModel):
    def __init__(self, dataset, reshape_size, model_save_path):
        super().__init__(GaussianNB(), dataset, reshape_size, model_save_path)