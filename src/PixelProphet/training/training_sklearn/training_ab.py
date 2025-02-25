from sklearn.ensemble import AdaBoostClassifier
from .training_basemodel import BaseModel


class AdaBoost(BaseModel):
    def __init__(self, model_save_path):
        super().__init__(AdaBoostClassifier(n_estimators=50), model_save_path)
