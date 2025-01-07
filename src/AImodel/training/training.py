from training.training_knnc import KNN
from training.training_svc import SVM
from training.training_rbf_svc import RBF_SVM
from training.training_gpc import GaussianProcess
from training.training_dtc import DecisionTree
from training.training_rfc import RandomForest
from training.training_mlpc import NeuralNet
from training.training_ab import AdaBoost
from training.training_nb import NaiveBayes
from training.training_qda import QDA  # Neu


class ModelTrainer:
    def __init__(self, dataset, reshape_size, model_save_path):
        self.dataset = dataset
        self.reshape_size = reshape_size
        self.model_save_path = model_save_path

        # Initialize all models
        self.KNN = KNN(self.dataset, self.reshape_size, self.model_save_path)
        self.SVM = SVM(self.dataset, self.reshape_size, self.model_save_path)
        self.RBF_SVM = RBF_SVM(self.dataset, self.reshape_size, self.model_save_path)
        self.GaussianProcess = GaussianProcess(self.dataset, self.reshape_size, self.model_save_path)
        self.DecisionTree = DecisionTree(self.dataset, self.reshape_size, self.model_save_path)
        self.RandomForest = RandomForest(self.dataset, self.reshape_size, self.model_save_path)
        self.NeuralNet = NeuralNet(self.dataset, self.reshape_size, self.model_save_path)
        self.AdaBoost = AdaBoost(self.dataset, self.reshape_size, self.model_save_path)
        self.NaiveBayes = NaiveBayes(self.dataset, self.reshape_size, self.model_save_path)
        # self.QDA = QDA(self.dataset, self.reshape_size, self.model_save_path)

    def train_all_models(self):
        # Train all models
        self.KNN.train()
        self.SVM.train()
        self.RBF_SVM.train()
        self.GaussianProcess.train()
        self.DecisionTree.train()
        self.RandomForest.train()
        self.NeuralNet.train()
        self.AdaBoost.train()
        self.NaiveBayes.train()
        # self.QDA.train()
        print("Training completed successfully")

    def show_prediction(self):
        # Show predictions for all models
        self.DecisionTree.predict()
        self.KNN.predict()
        self.SVM.predict()
        self.RBF_SVM.predict()
        self.GaussianProcess.predict()
        self.RandomForest.predict()
        self.NeuralNet.predict()
        self.AdaBoost.predict()
        self.NaiveBayes.predict()
        # self.QDA.predict()
        print("Predictions completed successfully")
