import torch
import re
import torch.nn as nn

from tensorflow.keras import layers, models


def adjust_input_channels_pytorch(model):
    modified = False
    try:
        # ResNet, EfficientNet, VisionTransformer (ViT)
        if hasattr(model, "conv1") and isinstance(model.conv1, nn.Conv2d):
            model.conv1 = nn.Conv2d(1, model.conv1.out_channels, kernel_size=model.conv1.kernel_size,
                                    stride=model.conv1.stride, padding=model.conv1.padding, bias=False)
            modified = True

        # DenseNet, MobileNet, SqueezeNet
        elif hasattr(model, "features") and hasattr(model.features, "conv0") and isinstance(model.features.conv0,
                                                                                            nn.Conv2d):
            model.features.conv0 = nn.Conv2d(1, model.features.conv0.out_channels,
                                             kernel_size=model.features.conv0.kernel_size,
                                             stride=model.features.conv0.stride, padding=model.features.conv0.padding,
                                             bias=False)
            modified = True

        # AlexNet, VGG
        elif hasattr(model, "features") and isinstance(model.features[0], nn.Conv2d):
            model.features[0] = nn.Conv2d(1, model.features[0].out_channels,
                                          kernel_size=model.features[0].kernel_size,
                                          stride=model.features[0].stride, padding=model.features[0].padding,
                                          bias=False)
            modified = True

        # Inception v3
        elif hasattr(model, "Conv2d_1a_3x3") and isinstance(model.Conv2d_1a_3x3.conv, nn.Conv2d):
            model.Conv2d_1a_3x3.conv = nn.Conv2d(1, model.Conv2d_1a_3x3.conv.out_channels,
                                                 kernel_size=model.Conv2d_1a_3x3.conv.kernel_size,
                                                 stride=model.Conv2d_1a_3x3.conv.stride,
                                                 padding=model.Conv2d_1a_3x3.conv.padding, bias=False)
            modified = True

        # ConvNeXt
        elif hasattr(model, "stem") and isinstance(model.stem[0], nn.Conv2d):
            model.stem[0] = nn.Conv2d(1, model.stem[0].out_channels,
                                      kernel_size=model.stem[0].kernel_size,
                                      stride=model.stem[0].stride, padding=model.stem[0].padding,
                                      bias=False)
            modified = True
    except Exception as e:
        print(f"⚠️ Model modification failed: {e}")
    return modified


def adjust_input_channels_pytorch_tensor(model, x_tensor, reshape_size):
    expected_channels = detect_input_channels_pytorch(model, reshape_size)
    modified = adjust_input_channels_pytorch(model)

    x_tensor = x_tensor.view(-1, 1, reshape_size, reshape_size)
    if not modified and expected_channels != 1:
        print(
            f"🔹 Model expects {expected_channels} channels → Updating gray scale images to {expected_channels} channels.")
        x_tensor = x_tensor.repeat(1, expected_channels, 1, 1)
    return x_tensor


def adjust_model_output_layer_pytorch(model, num_classes, device, model_name):
    if hasattr(model, "classifier"):  # AlexNet, VGG
        if isinstance(model.classifier, nn.Sequential) and len(model.classifier) > 6:
            current_classes = model.classifier[6].out_features
            if current_classes != num_classes:
                print(f"Update classifier[6]: {current_classes} → {num_classes}")
                model.classifier[6] = nn.Linear(model.classifier[6].in_features, num_classes).to(device)

    elif hasattr(model, "fc"):  # ResNet, ConvNeXt, EfficientNet
        current_fc = model.fc.out_features
        if current_fc != num_classes:
            print(f"Update fc-Layer: {current_fc} → {num_classes}")
            model.fc = nn.Linear(model.fc.in_features, num_classes).to(device)

    elif hasattr(model, "head"):  # ConvNeXt & bestimmte Architekturen
        current_head = model.head.out_features
        if current_head != num_classes:
            print(f"Update head-Layer: {current_head} → {num_classes}")
            model.head = nn.Linear(model.head.in_features, num_classes).to(device)

    elif isinstance(model, nn.Module):  # Allgemeiner Check für PyTorch Modelle
        # Überprüfe den Namen des Modells und passe die finale Schicht an
        if "efficientnet" in model_name:  # Spezifische Behandlung für EfficientNet
            if hasattr(model, 'classifier'):
                current_classifier = model.classifier[-1]
                if current_classifier.out_features != num_classes:
                    print(f"Update classifier[-1]: {current_classifier.out_features} → {num_classes}")
                    model.classifier[-1] = nn.Linear(model.classifier[-1].in_features,
                                                          num_classes).to(device)
    else:
        raise ValueError("Unknown model architecture. Final Layer could not get updated.")
    return model


def adjust_model_output_layer_keras(model, num_classes, model_name):
    if hasattr(model, 'layers'):
        # Für Dense-Schicht als letzte Schicht (typisch für viele Modelle)
        if isinstance(model.layers[-1], layers.Dense):
            current_classes = model.layers[-1].units
            if current_classes != num_classes:
                print(f"🔹 Update Dense output layer: {current_classes} → {num_classes}")
                model.layers[-1] = layers.Dense(num_classes, activation='softmax')(model.layers[-2].output)
                model = models.Model(inputs=model.inputs, outputs=model.layers[-1].output)

        # **ResNet, VGG, Inception** (und ähnliche Architekturen, die mit fc arbeiten)
        elif hasattr(model, 'fc'):  # Einige Modelle wie ResNet, ConvNeXt, EfficientNet
            current_fc = model.fc.units  # In Keras sind diese Schichten als Dense zu finden
            if current_fc != num_classes:
                print(f"🔹 Update fc-Layer: {current_fc} → {num_classes}")
                model.fc = layers.Dense(num_classes, activation='softmax')(model.fc.input)
                model = models.Model(inputs=model.inputs, outputs=model.fc.output)

        # **EfficientNet** - Spezifische Behandlung für Modelle wie EfficientNet
        elif 'efficientnet' in model_name.lower():
            if hasattr(model, 'classifier'):
                current_classifier = model.classifier[-1]  # Für Keras Modelle
                if current_classifier.units != num_classes:
                    print(f"🔹 Update classifier[-1]: {current_classifier.units} → {num_classes}")
                    model.classifier[-1] = layers.Dense(num_classes, activation='softmax')(model.classifier[-2].output)
                    model = models.Model(inputs=model.inputs, outputs=model.classifier[-1].output)

        # **InceptionV3** - Überprüfen auf GlobalAveragePooling und dann Dense-Schicht
        elif 'inceptionv3' in model_name.lower():
            if hasattr(model, 'output'):
                if model.output.shape[-1] != num_classes:
                    print(f"🔹 Update InceptionV3 output layer: {model.output.shape[-1]} → {num_classes}")
                    x = layers.GlobalAveragePooling2D()(model.output)
                    output = layers.Dense(num_classes, activation='softmax')(x)
                    model = models.Model(inputs=model.inputs, outputs=output)

        # **Xception** - Ebenfalls GlobalAveragePooling2D und Dense
        elif 'xception' in model_name.lower():
            if hasattr(model, 'output'):
                if model.output.shape[-1] != num_classes:
                    print(f"🔹 Update Xception output layer: {model.output.shape[-1]} → {num_classes}")
                    x = layers.GlobalAveragePooling2D()(model.output)
                    output = layers.Dense(num_classes, activation='softmax')(x)
                    model = models.Model(inputs=model.inputs, outputs=output)

        # **MobileNetV2** - MobileNetV2 hat eine ähnliche Struktur wie EfficientNet und Inception
        elif 'mobilenetv2' in model_name.lower():
            if hasattr(model, 'output'):
                if model.output.shape[-1] != num_classes:
                    print(f"🔹 Update MobileNetV2 output layer: {model.output.shape[-1]} → {num_classes}")
                    x = layers.GlobalAveragePooling2D()(model.output)
                    output = layers.Dense(num_classes, activation='softmax')(x)
                    model = models.Model(inputs=model.inputs, outputs=output)

        # **Allgemeine Fälle**
        else:
            raise ValueError("Unknown model architecture. Final Layer could not get updated.")
    return model


def detect_input_channels_pytorch(model, reshape_size):
    try:
        model(torch.empty(1, 0, reshape_size, reshape_size))
    except RuntimeError as e:
        search = re.search(r'(\d+)[^\d]+channels', str(e))
        if search:
            return int(search.group(1))
    return 1


