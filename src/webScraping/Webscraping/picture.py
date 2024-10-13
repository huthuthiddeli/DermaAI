import base64


class Picture:

    def __init__(self, pic, diagnosis):
        self.picture = pic
        self.diagnosis = diagnosis

    def to_dict(self):
        return {
            "picture": self.picture.decode('utf-8'),
            "diagnosis": self.diagnosis.decode('utf-8')
        }