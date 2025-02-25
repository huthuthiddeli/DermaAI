from sklearn.tree import DecisionTreeClassifier
from .training_basemodel import BaseModel


class DecisionTree(BaseModel):
    def __init__(self, model_save_path):
        super().__init__(DecisionTreeClassifier(), model_save_path)
