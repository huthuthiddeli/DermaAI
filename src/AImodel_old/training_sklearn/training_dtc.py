from sklearn.tree import DecisionTreeClassifier
from .training_basemodel import BaseModel


class DecisionTree(BaseModel):
    def __init__(self, reshape_size, model_save_path):
        super().__init__(DecisionTreeClassifier(), reshape_size, model_save_path)
