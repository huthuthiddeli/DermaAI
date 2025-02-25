from sklearn.ensemble import RandomForestClassifier
from .training_basemodel import BaseModel


class RandomForest(BaseModel):
    def __init__(self, model_save_path):
        super().__init__(RandomForestClassifier(n_estimators=100), model_save_path)
