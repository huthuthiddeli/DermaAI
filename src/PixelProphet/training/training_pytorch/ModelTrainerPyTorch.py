from .training_alexnet import AlexNetModel
from .training_convnext import ConvNeXtModel
from .training_densenet import DenseNetModel
from .training_efficientnet import EfficientNetModel
from .training_efficientnetv2 import EfficientNetV2Model
from .training_googlenet import GoogLeNetModel
from .training_inception import InceptionModel
from .training_maxvit import MaxVitModel
from .training_mnasnet import MNASNetModel
from .training_mobilenetv2 import MobileNetV2Model
from .training_mobilenetv3 import MobileNetV3Model
from .training_regnet import RegNetModel
from .training_resnet import ResNetModel
from .training_resnext import ResNeXtModel
from .training_shufflenet import ShuffleNetModel
from .training_squeezenet import SqueezeNetModel
from .training_swintransformer import SwinTransformerModel
from .training_vgg import VGGModel
from .training_visiontransformer import VisionTransformerModel
from .training_wideresnet import WideResNetModel
from ..BaseModelTrainer import ModelTrainer


class ModelTrainerPyTorch(ModelTrainer):
    def __init__(self, model_save_path, num_classes):
        super().__init__([
            AlexNetModel(model_save_path, num_classes),
            ConvNeXtModel(model_save_path, num_classes),
            DenseNetModel(model_save_path, num_classes),
            EfficientNetModel(model_save_path, num_classes),
            EfficientNetV2Model(model_save_path, num_classes),
            GoogLeNetModel(model_save_path, num_classes),
            InceptionModel(model_save_path, num_classes),
            MaxVitModel(model_save_path, num_classes),
            MNASNetModel(model_save_path, num_classes),
            MobileNetV2Model(model_save_path, num_classes),
            MobileNetV3Model(model_save_path, num_classes),
            RegNetModel(model_save_path, num_classes),
            ResNetModel(model_save_path, num_classes),
            ResNeXtModel(model_save_path, num_classes),
            ShuffleNetModel(model_save_path, num_classes),
            SqueezeNetModel(model_save_path, num_classes),
            SwinTransformerModel(model_save_path, num_classes),
            VGGModel(model_save_path, num_classes),
            VisionTransformerModel(model_save_path, num_classes),
            WideResNetModel(model_save_path, num_classes)
        ], self.__class__.__name__)
