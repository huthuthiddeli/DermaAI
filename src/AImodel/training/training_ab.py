from sklearn.ensemble import AdaBoostClassifier
from training.training_basemodel import BaseModel


class AdaBoost(BaseModel):
    def __init__(self, dataset, reshape_size, model_save_path):
        super().__init__(AdaBoostClassifier(n_estimators=50), dataset, reshape_size, model_save_path)
