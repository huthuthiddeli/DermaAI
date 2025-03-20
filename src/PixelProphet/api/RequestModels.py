from pydantic import BaseModel


class UserRequest(BaseModel):
    email: str
    password: str
    mfa: bool


class PredictionRequest(BaseModel):
    model_int: int
    trainer_string: str
    image: str


class ClassifierReportRequest(BaseModel):
    email: str
    password: str
    trainer_string: str
    model_int: int
    reshape_size: int


class ClassifierReportsRequest(BaseModel):
    email: str
    password: str
    reshape_size: int


class RetrainAllRequest(BaseModel):
    email: str
    password: str
    num_epochs: int
    reshape_size: int


class RetrainRequest(BaseModel):
    email: str
    password: str
    trainer_string: str
    model_int: int
    num_epochs: int
    reshape_size: int


class SavePredictionRequest(BaseModel):
    email: str
    password: str
    image: str
    prediction: dict


class ResizeRequest(BaseModel):
    image: str

