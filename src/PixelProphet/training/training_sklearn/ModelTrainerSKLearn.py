from .training_knnc import KNN
from .training_svc import SVM
from .training_rbf_svc import RBF_SVM
from .training_gpc import GaussianProcess
from .training_dtc import DecisionTree
from .training_rfc import RandomForest
from .training_mlpc import NeuralNet
from .training_ab import AdaBoost
from .training_nb import NaiveBayes
from ..BaseModelTrainer import ModelTrainer


class ModelTrainerSKLearn(ModelTrainer):
    def __init__(self, model_save_path):
        super().__init__([
            KNN(model_save_path),
            SVM(model_save_path),
            RBF_SVM(model_save_path),
            GaussianProcess(model_save_path),
            DecisionTree(model_save_path),
            RandomForest(model_save_path),
            NeuralNet(model_save_path),
            AdaBoost(model_save_path),
            NaiveBayes(model_save_path)
        ], self.__class__.__name__)
