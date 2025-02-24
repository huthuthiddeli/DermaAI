from sklearn.ensemble import RandomForestClassifier
from .training_basemodel import BaseModel


class RandomForest(BaseModel):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(RandomForestClassifier(n_estimators=100), reshape_size, model_save_path)
